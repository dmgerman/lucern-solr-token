begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search.facet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|facet
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
name|IdentityHashMap
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
name|LinkedHashMap
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
name|search
operator|.
name|BooleanClause
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
name|BooleanQuery
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
name|MatchAllDocsQuery
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
name|util
operator|.
name|SimpleOrderedMap
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
name|handler
operator|.
name|component
operator|.
name|ResponseBuilder
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
name|request
operator|.
name|SolrRequestInfo
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
name|SchemaField
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
name|BitDocSet
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
name|DocIterator
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
name|QParser
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
name|search
operator|.
name|SyntaxError
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
name|RTimer
import|;
end_import
begin_class
DECL|class|FacetProcessor
specifier|public
class|class
name|FacetProcessor
parameter_list|<
name|FacetRequestT
extends|extends
name|FacetRequest
parameter_list|>
block|{
DECL|field|response
specifier|protected
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|response
decl_stmt|;
DECL|field|fcontext
specifier|protected
name|FacetContext
name|fcontext
decl_stmt|;
DECL|field|freq
specifier|protected
name|FacetRequestT
name|freq
decl_stmt|;
DECL|field|accMap
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|SlotAcc
argument_list|>
name|accMap
decl_stmt|;
DECL|field|accs
specifier|protected
name|SlotAcc
index|[]
name|accs
decl_stmt|;
DECL|field|countAcc
specifier|protected
name|CountSlotAcc
name|countAcc
decl_stmt|;
DECL|method|FacetProcessor
name|FacetProcessor
parameter_list|(
name|FacetContext
name|fcontext
parameter_list|,
name|FacetRequestT
name|freq
parameter_list|)
block|{
name|this
operator|.
name|fcontext
operator|=
name|fcontext
expr_stmt|;
name|this
operator|.
name|freq
operator|=
name|freq
expr_stmt|;
block|}
DECL|method|process
specifier|public
name|void
name|process
parameter_list|()
throws|throws
name|IOException
block|{
name|handleDomainChanges
argument_list|()
expr_stmt|;
block|}
DECL|method|handleDomainChanges
specifier|protected
name|void
name|handleDomainChanges
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|freq
operator|.
name|domain
operator|==
literal|null
condition|)
return|return;
name|handleFilterExclusions
argument_list|()
expr_stmt|;
name|handleBlockJoin
argument_list|()
expr_stmt|;
block|}
DECL|method|handleBlockJoin
specifier|private
name|void
name|handleBlockJoin
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
operator|(
name|freq
operator|.
name|domain
operator|.
name|toChildren
operator|||
name|freq
operator|.
name|domain
operator|.
name|toParent
operator|)
condition|)
return|return;
comment|// TODO: avoid query parsing per-bucket somehow...
name|String
name|parentStr
init|=
name|freq
operator|.
name|domain
operator|.
name|parents
decl_stmt|;
name|Query
name|parentQuery
decl_stmt|;
try|try
block|{
name|QParser
name|parser
init|=
name|QParser
operator|.
name|getParser
argument_list|(
name|parentStr
argument_list|,
literal|null
argument_list|,
name|fcontext
operator|.
name|req
argument_list|)
decl_stmt|;
name|parentQuery
operator|=
name|parser
operator|.
name|getQuery
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SyntaxError
name|err
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Error parsing block join parent specification: "
operator|+
name|parentStr
argument_list|)
throw|;
block|}
name|BitDocSet
name|parents
init|=
name|fcontext
operator|.
name|searcher
operator|.
name|getDocSetBits
argument_list|(
name|parentQuery
argument_list|)
decl_stmt|;
name|DocSet
name|input
init|=
name|fcontext
operator|.
name|base
decl_stmt|;
name|DocSet
name|result
decl_stmt|;
if|if
condition|(
name|freq
operator|.
name|domain
operator|.
name|toChildren
condition|)
block|{
name|DocSet
name|filt
init|=
name|fcontext
operator|.
name|searcher
operator|.
name|getDocSetBits
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|)
decl_stmt|;
name|result
operator|=
name|BlockJoin
operator|.
name|toChildren
argument_list|(
name|input
argument_list|,
name|parents
argument_list|,
name|filt
argument_list|,
name|fcontext
operator|.
name|qcontext
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|BlockJoin
operator|.
name|toParents
argument_list|(
name|input
argument_list|,
name|parents
argument_list|,
name|fcontext
operator|.
name|qcontext
argument_list|)
expr_stmt|;
block|}
name|fcontext
operator|.
name|base
operator|=
name|result
expr_stmt|;
block|}
DECL|method|handleFilterExclusions
specifier|private
name|void
name|handleFilterExclusions
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|excludeTags
init|=
name|freq
operator|.
name|domain
operator|.
name|excludeTags
decl_stmt|;
if|if
condition|(
name|excludeTags
operator|==
literal|null
operator|||
name|excludeTags
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return;
block|}
comment|// TODO: somehow remove responsebuilder dependency
name|ResponseBuilder
name|rb
init|=
name|SolrRequestInfo
operator|.
name|getRequestInfo
argument_list|()
operator|.
name|getResponseBuilder
argument_list|()
decl_stmt|;
name|Map
name|tagMap
init|=
operator|(
name|Map
operator|)
name|rb
operator|.
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
literal|"tags"
argument_list|)
decl_stmt|;
if|if
condition|(
name|tagMap
operator|==
literal|null
condition|)
block|{
comment|// no filters were tagged
return|return;
block|}
name|IdentityHashMap
argument_list|<
name|Query
argument_list|,
name|Boolean
argument_list|>
name|excludeSet
init|=
operator|new
name|IdentityHashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|excludeTag
range|:
name|excludeTags
control|)
block|{
name|Object
name|olst
init|=
name|tagMap
operator|.
name|get
argument_list|(
name|excludeTag
argument_list|)
decl_stmt|;
comment|// tagMap has entries of List<String,List<QParser>>, but subject to change in the future
if|if
condition|(
operator|!
operator|(
name|olst
operator|instanceof
name|Collection
operator|)
condition|)
continue|continue;
for|for
control|(
name|Object
name|o
range|:
operator|(
name|Collection
argument_list|<
name|?
argument_list|>
operator|)
name|olst
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|QParser
operator|)
condition|)
continue|continue;
name|QParser
name|qp
init|=
operator|(
name|QParser
operator|)
name|o
decl_stmt|;
try|try
block|{
name|excludeSet
operator|.
name|put
argument_list|(
name|qp
operator|.
name|getQuery
argument_list|()
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SyntaxError
name|syntaxError
parameter_list|)
block|{
comment|// This should not happen since we should only be retrieving a previously parsed query
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|syntaxError
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|excludeSet
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return;
name|List
argument_list|<
name|Query
argument_list|>
name|qlist
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// add the base query
if|if
condition|(
operator|!
name|excludeSet
operator|.
name|containsKey
argument_list|(
name|rb
operator|.
name|getQuery
argument_list|()
argument_list|)
condition|)
block|{
name|qlist
operator|.
name|add
argument_list|(
name|rb
operator|.
name|getQuery
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// add the filters
if|if
condition|(
name|rb
operator|.
name|getFilters
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Query
name|q
range|:
name|rb
operator|.
name|getFilters
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|excludeSet
operator|.
name|containsKey
argument_list|(
name|q
argument_list|)
condition|)
block|{
name|qlist
operator|.
name|add
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// now walk back up the context tree
comment|// TODO: we lose parent exclusions...
for|for
control|(
name|FacetContext
name|curr
init|=
name|fcontext
init|;
name|curr
operator|!=
literal|null
condition|;
name|curr
operator|=
name|curr
operator|.
name|parent
control|)
block|{
if|if
condition|(
name|curr
operator|.
name|filter
operator|!=
literal|null
condition|)
block|{
name|qlist
operator|.
name|add
argument_list|(
name|curr
operator|.
name|filter
argument_list|)
expr_stmt|;
block|}
block|}
comment|// recompute the base domain
name|fcontext
operator|.
name|base
operator|=
name|fcontext
operator|.
name|searcher
operator|.
name|getDocSet
argument_list|(
name|qlist
argument_list|)
expr_stmt|;
block|}
DECL|method|getResponse
specifier|public
name|Object
name|getResponse
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|createAccs
specifier|protected
name|void
name|createAccs
parameter_list|(
name|int
name|docCount
parameter_list|,
name|int
name|slotCount
parameter_list|)
throws|throws
name|IOException
block|{
name|accMap
operator|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
expr_stmt|;
comment|// allow a custom count acc to be used
if|if
condition|(
name|countAcc
operator|==
literal|null
condition|)
block|{
name|countAcc
operator|=
operator|new
name|CountSlotArrAcc
argument_list|(
name|fcontext
argument_list|,
name|slotCount
argument_list|)
expr_stmt|;
name|countAcc
operator|.
name|key
operator|=
literal|"count"
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|AggValueSource
argument_list|>
name|entry
range|:
name|freq
operator|.
name|getFacetStats
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|SlotAcc
name|acc
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|createSlotAcc
argument_list|(
name|fcontext
argument_list|,
name|docCount
argument_list|,
name|slotCount
argument_list|)
decl_stmt|;
name|acc
operator|.
name|key
operator|=
name|entry
operator|.
name|getKey
argument_list|()
expr_stmt|;
name|accMap
operator|.
name|put
argument_list|(
name|acc
operator|.
name|key
argument_list|,
name|acc
argument_list|)
expr_stmt|;
block|}
name|accs
operator|=
operator|new
name|SlotAcc
index|[
name|accMap
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SlotAcc
name|acc
range|:
name|accMap
operator|.
name|values
argument_list|()
control|)
block|{
name|accs
index|[
name|i
operator|++
index|]
operator|=
name|acc
expr_stmt|;
block|}
block|}
DECL|method|resetStats
specifier|protected
name|void
name|resetStats
parameter_list|()
block|{
name|countAcc
operator|.
name|reset
argument_list|()
expr_stmt|;
for|for
control|(
name|SlotAcc
name|acc
range|:
name|accs
control|)
block|{
name|acc
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|processStats
specifier|protected
name|void
name|processStats
parameter_list|(
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|bucket
parameter_list|,
name|DocSet
name|docs
parameter_list|,
name|int
name|docCount
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|docCount
operator|==
literal|0
operator|&&
operator|!
name|freq
operator|.
name|processEmpty
operator|||
name|freq
operator|.
name|getFacetStats
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|bucket
operator|.
name|add
argument_list|(
literal|"count"
argument_list|,
name|docCount
argument_list|)
expr_stmt|;
return|return;
block|}
name|createAccs
argument_list|(
name|docCount
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|int
name|collected
init|=
name|collect
argument_list|(
name|docs
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|countAcc
operator|.
name|incrementCount
argument_list|(
literal|0
argument_list|,
name|collected
argument_list|)
expr_stmt|;
assert|assert
name|collected
operator|==
name|docCount
assert|;
name|addStats
argument_list|(
name|bucket
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|processSubs
specifier|protected
name|void
name|processSubs
parameter_list|(
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|response
parameter_list|,
name|Query
name|filter
parameter_list|,
name|DocSet
name|domain
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO: what if a zero bucket has a sub-facet with an exclusion that would yield results?
comment|// should we check for domain-altering exclusions, or even ask the sub-facet for
comment|// it's domain and then only skip it if it's 0?
if|if
condition|(
name|domain
operator|==
literal|null
operator|||
name|domain
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|&&
operator|!
name|freq
operator|.
name|processEmpty
condition|)
block|{
return|return;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|FacetRequest
argument_list|>
name|sub
range|:
name|freq
operator|.
name|getSubFacets
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
comment|// make a new context for each sub-facet since they can change the domain
name|FacetContext
name|subContext
init|=
name|fcontext
operator|.
name|sub
argument_list|(
name|filter
argument_list|,
name|domain
argument_list|)
decl_stmt|;
name|FacetProcessor
name|subProcessor
init|=
name|sub
operator|.
name|getValue
argument_list|()
operator|.
name|createFacetProcessor
argument_list|(
name|subContext
argument_list|)
decl_stmt|;
if|if
condition|(
name|fcontext
operator|.
name|getDebugInfo
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// if fcontext.debugInfo != null, it means rb.debug() == true
name|FacetDebugInfo
name|fdebug
init|=
operator|new
name|FacetDebugInfo
argument_list|()
decl_stmt|;
name|subContext
operator|.
name|setDebugInfo
argument_list|(
name|fdebug
argument_list|)
expr_stmt|;
name|fcontext
operator|.
name|getDebugInfo
argument_list|()
operator|.
name|addChild
argument_list|(
name|fdebug
argument_list|)
expr_stmt|;
name|fdebug
operator|.
name|setReqDescription
argument_list|(
name|sub
operator|.
name|getValue
argument_list|()
operator|.
name|getFacetDescription
argument_list|()
argument_list|)
expr_stmt|;
name|fdebug
operator|.
name|setProcessor
argument_list|(
name|subProcessor
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|subContext
operator|.
name|filter
operator|!=
literal|null
condition|)
name|fdebug
operator|.
name|setFilter
argument_list|(
name|subContext
operator|.
name|filter
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|RTimer
name|timer
init|=
operator|new
name|RTimer
argument_list|()
decl_stmt|;
name|subProcessor
operator|.
name|process
argument_list|()
expr_stmt|;
name|long
name|timeElapsed
init|=
operator|(
name|long
operator|)
name|timer
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|fdebug
operator|.
name|setElapse
argument_list|(
name|timeElapsed
argument_list|)
expr_stmt|;
name|fdebug
operator|.
name|putInfoItem
argument_list|(
literal|"domainSize"
argument_list|,
operator|(
name|long
operator|)
name|subContext
operator|.
name|base
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|subProcessor
operator|.
name|process
argument_list|()
expr_stmt|;
block|}
name|response
operator|.
name|add
argument_list|(
name|sub
operator|.
name|getKey
argument_list|()
argument_list|,
name|subProcessor
operator|.
name|getResponse
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|collect
name|int
name|collect
parameter_list|(
name|DocSet
name|docs
parameter_list|,
name|int
name|slot
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
name|SolrIndexSearcher
name|searcher
init|=
name|fcontext
operator|.
name|searcher
decl_stmt|;
specifier|final
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
init|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|leaves
argument_list|()
decl_stmt|;
specifier|final
name|Iterator
argument_list|<
name|LeafReaderContext
argument_list|>
name|ctxIt
init|=
name|leaves
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|LeafReaderContext
name|ctx
init|=
literal|null
decl_stmt|;
name|int
name|segBase
init|=
literal|0
decl_stmt|;
name|int
name|segMax
decl_stmt|;
name|int
name|adjustedMax
init|=
literal|0
decl_stmt|;
for|for
control|(
name|DocIterator
name|docsIt
init|=
name|docs
operator|.
name|iterator
argument_list|()
init|;
name|docsIt
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|int
name|doc
init|=
name|docsIt
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|>=
name|adjustedMax
condition|)
block|{
do|do
block|{
name|ctx
operator|=
name|ctxIt
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|ctx
operator|==
literal|null
condition|)
block|{
comment|// should be impossible
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"INTERNAL FACET ERROR"
argument_list|)
throw|;
block|}
name|segBase
operator|=
name|ctx
operator|.
name|docBase
expr_stmt|;
name|segMax
operator|=
name|ctx
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
name|adjustedMax
operator|=
name|segBase
operator|+
name|segMax
expr_stmt|;
block|}
do|while
condition|(
name|doc
operator|>=
name|adjustedMax
condition|)
do|;
assert|assert
name|doc
operator|>=
name|ctx
operator|.
name|docBase
assert|;
name|setNextReader
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
name|count
operator|++
expr_stmt|;
name|collect
argument_list|(
name|doc
operator|-
name|segBase
argument_list|,
name|slot
argument_list|)
expr_stmt|;
comment|// per-seg collectors
block|}
return|return
name|count
return|;
block|}
DECL|method|collect
name|void
name|collect
parameter_list|(
name|int
name|segDoc
parameter_list|,
name|int
name|slot
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|accs
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|SlotAcc
name|acc
range|:
name|accs
control|)
block|{
name|acc
operator|.
name|collect
argument_list|(
name|segDoc
argument_list|,
name|slot
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|setNextReader
name|void
name|setNextReader
parameter_list|(
name|LeafReaderContext
name|ctx
parameter_list|)
throws|throws
name|IOException
block|{
comment|// countAcc.setNextReader is a no-op
for|for
control|(
name|SlotAcc
name|acc
range|:
name|accs
control|)
block|{
name|acc
operator|.
name|setNextReader
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addStats
name|void
name|addStats
parameter_list|(
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|target
parameter_list|,
name|int
name|slotNum
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|count
init|=
name|countAcc
operator|.
name|getCount
argument_list|(
name|slotNum
argument_list|)
decl_stmt|;
name|target
operator|.
name|add
argument_list|(
literal|"count"
argument_list|,
name|count
argument_list|)
expr_stmt|;
if|if
condition|(
name|count
operator|>
literal|0
operator|||
name|freq
operator|.
name|processEmpty
condition|)
block|{
for|for
control|(
name|SlotAcc
name|acc
range|:
name|accs
control|)
block|{
name|acc
operator|.
name|setValues
argument_list|(
name|target
argument_list|,
name|slotNum
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|fillBucket
specifier|public
name|void
name|fillBucket
parameter_list|(
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|bucket
parameter_list|,
name|Query
name|q
parameter_list|,
name|DocSet
name|result
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|needDocSet
init|=
name|freq
operator|.
name|getFacetStats
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|||
name|freq
operator|.
name|getSubFacets
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
decl_stmt|;
comment|// TODO: always collect counts or not???
name|int
name|count
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|count
operator|=
name|result
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|needDocSet
condition|)
block|{
if|if
condition|(
name|q
operator|==
literal|null
condition|)
block|{
name|result
operator|=
name|fcontext
operator|.
name|base
expr_stmt|;
comment|// result.incref(); // OFF-HEAP
block|}
else|else
block|{
name|result
operator|=
name|fcontext
operator|.
name|searcher
operator|.
name|getDocSet
argument_list|(
name|q
argument_list|,
name|fcontext
operator|.
name|base
argument_list|)
expr_stmt|;
block|}
name|count
operator|=
name|result
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|q
operator|==
literal|null
condition|)
block|{
name|count
operator|=
name|fcontext
operator|.
name|base
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|count
operator|=
name|fcontext
operator|.
name|searcher
operator|.
name|numDocs
argument_list|(
name|q
argument_list|,
name|fcontext
operator|.
name|base
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|processStats
argument_list|(
name|bucket
argument_list|,
name|result
argument_list|,
operator|(
name|int
operator|)
name|count
argument_list|)
expr_stmt|;
name|processSubs
argument_list|(
name|bucket
argument_list|,
name|q
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
comment|// result.decref(); // OFF-HEAP
name|result
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
DECL|method|getFieldMissing
specifier|public
specifier|static
name|DocSet
name|getFieldMissing
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|DocSet
name|docs
parameter_list|,
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
name|SchemaField
name|sf
init|=
name|searcher
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|DocSet
name|hasVal
init|=
name|searcher
operator|.
name|getDocSet
argument_list|(
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|getRangeQuery
argument_list|(
literal|null
argument_list|,
name|sf
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|DocSet
name|answer
init|=
name|docs
operator|.
name|andNot
argument_list|(
name|hasVal
argument_list|)
decl_stmt|;
comment|// hasVal.decref(); // OFF-HEAP
return|return
name|answer
return|;
block|}
DECL|method|getFieldMissingQuery
specifier|public
specifier|static
name|Query
name|getFieldMissingQuery
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
name|SchemaField
name|sf
init|=
name|searcher
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|Query
name|hasVal
init|=
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|getRangeQuery
argument_list|(
literal|null
argument_list|,
name|sf
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|noVal
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|noVal
operator|.
name|add
argument_list|(
name|hasVal
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
return|return
name|noVal
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class
end_unit
