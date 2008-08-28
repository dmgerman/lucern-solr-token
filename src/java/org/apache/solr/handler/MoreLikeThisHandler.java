begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
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
name|io
operator|.
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|Comparator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|document
operator|.
name|Document
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
name|IndexReader
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
name|Term
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
name|search
operator|.
name|TermQuery
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
name|similar
operator|.
name|MoreLikeThis
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
name|CommonParams
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
name|DisMaxParams
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
name|params
operator|.
name|MoreLikeThisParams
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
name|SolrParams
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
name|MoreLikeThisParams
operator|.
name|TermStyle
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
name|ContentStream
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
name|core
operator|.
name|SolrCore
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
name|SimpleFacets
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
name|SolrQueryRequest
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
name|SolrQueryResponse
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
name|IndexSchema
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
name|DocList
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
name|DocListAndSet
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
name|QueryParsing
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
name|SolrPluginUtils
import|;
end_import
begin_comment
comment|/**  * Solr MoreLikeThis --  *   * Return similar documents either based on a single document or based on posted text.  *   * @since solr 1.3  */
end_comment
begin_class
DECL|class|MoreLikeThisHandler
specifier|public
class|class
name|MoreLikeThisHandler
extends|extends
name|RequestHandlerBase
block|{
comment|// Pattern is thread safe -- TODO? share this with general 'fl' param
DECL|field|splitList
specifier|private
specifier|static
specifier|final
name|Pattern
name|splitList
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|",| "
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|SolrIndexSearcher
name|searcher
init|=
name|req
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
comment|// Parse Required Params
comment|// This will either have a single Reader or valid query
name|Reader
name|reader
init|=
literal|null
decl_stmt|;
name|String
name|q
init|=
name|params
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|)
decl_stmt|;
if|if
condition|(
name|q
operator|==
literal|null
operator|||
name|q
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|<
literal|1
condition|)
block|{
name|Iterable
argument_list|<
name|ContentStream
argument_list|>
name|streams
init|=
name|req
operator|.
name|getContentStreams
argument_list|()
decl_stmt|;
if|if
condition|(
name|streams
operator|!=
literal|null
condition|)
block|{
name|Iterator
argument_list|<
name|ContentStream
argument_list|>
name|iter
init|=
name|streams
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|reader
operator|=
name|iter
operator|.
name|next
argument_list|()
operator|.
name|getReader
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
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
literal|"MoreLikeThis does not support multiple ContentStreams"
argument_list|)
throw|;
block|}
block|}
block|}
name|MoreLikeThisHelper
name|mlt
init|=
operator|new
name|MoreLikeThisHelper
argument_list|(
name|params
argument_list|,
name|searcher
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Query
argument_list|>
name|filters
init|=
name|SolrPluginUtils
operator|.
name|parseFilterQueries
argument_list|(
name|req
argument_list|)
decl_stmt|;
comment|// Hold on to the interesting terms if relevant
name|TermStyle
name|termStyle
init|=
name|TermStyle
operator|.
name|get
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|MoreLikeThisParams
operator|.
name|INTERESTING_TERMS
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|InterestingTerm
argument_list|>
name|interesting
init|=
operator|(
name|termStyle
operator|==
name|TermStyle
operator|.
name|NONE
operator|)
condition|?
literal|null
else|:
operator|new
name|ArrayList
argument_list|<
name|InterestingTerm
argument_list|>
argument_list|(
name|mlt
operator|.
name|mlt
operator|.
name|getMaxQueryTerms
argument_list|()
argument_list|)
decl_stmt|;
comment|// What fields do we need to return
name|String
name|fl
init|=
name|params
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|FL
argument_list|)
decl_stmt|;
name|int
name|flags
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|fl
operator|!=
literal|null
condition|)
block|{
name|flags
operator||=
name|SolrPluginUtils
operator|.
name|setReturnFields
argument_list|(
name|fl
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
name|int
name|start
init|=
name|params
operator|.
name|getInt
argument_list|(
name|CommonParams
operator|.
name|START
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|int
name|rows
init|=
name|params
operator|.
name|getInt
argument_list|(
name|CommonParams
operator|.
name|ROWS
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|DocListAndSet
name|mltDocs
init|=
literal|null
decl_stmt|;
comment|// Find documents MoreLikeThis - either with a reader or a query
comment|//--------------------------------------------------------------------------------
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|mltDocs
operator|=
name|mlt
operator|.
name|getMoreLikeThis
argument_list|(
name|reader
argument_list|,
name|start
argument_list|,
name|rows
argument_list|,
name|filters
argument_list|,
name|interesting
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|q
operator|!=
literal|null
condition|)
block|{
comment|// Matching options
name|boolean
name|includeMatch
init|=
name|params
operator|.
name|getBool
argument_list|(
name|MoreLikeThisParams
operator|.
name|MATCH_INCLUDE
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|int
name|matchOffset
init|=
name|params
operator|.
name|getInt
argument_list|(
name|MoreLikeThisParams
operator|.
name|MATCH_OFFSET
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|// Find the base match
name|Query
name|query
init|=
name|QueryParsing
operator|.
name|parseQuery
argument_list|(
name|q
argument_list|,
name|params
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|DF
argument_list|)
argument_list|,
name|params
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
argument_list|)
decl_stmt|;
name|DocList
name|match
init|=
name|searcher
operator|.
name|getDocList
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|matchOffset
argument_list|,
literal|1
argument_list|,
name|flags
argument_list|)
decl_stmt|;
comment|// only get the first one...
if|if
condition|(
name|includeMatch
condition|)
block|{
name|rsp
operator|.
name|add
argument_list|(
literal|"match"
argument_list|,
name|match
argument_list|)
expr_stmt|;
block|}
comment|// This is an iterator, but we only handle the first match
name|DocIterator
name|iterator
init|=
name|match
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
comment|// do a MoreLikeThis query for each document in results
name|int
name|id
init|=
name|iterator
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|mltDocs
operator|=
name|mlt
operator|.
name|getMoreLikeThis
argument_list|(
name|id
argument_list|,
name|start
argument_list|,
name|rows
argument_list|,
name|filters
argument_list|,
name|interesting
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
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
name|BAD_REQUEST
argument_list|,
literal|"MoreLikeThis requires either a query (?q=) or text to find similar documents."
argument_list|)
throw|;
block|}
if|if
condition|(
name|mltDocs
operator|==
literal|null
condition|)
block|{
name|mltDocs
operator|=
operator|new
name|DocListAndSet
argument_list|()
expr_stmt|;
comment|// avoid NPE
block|}
name|rsp
operator|.
name|add
argument_list|(
literal|"response"
argument_list|,
name|mltDocs
operator|.
name|docList
argument_list|)
expr_stmt|;
if|if
condition|(
name|interesting
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|termStyle
operator|==
name|TermStyle
operator|.
name|DETAILS
condition|)
block|{
name|NamedList
argument_list|<
name|Float
argument_list|>
name|it
init|=
operator|new
name|NamedList
argument_list|<
name|Float
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|InterestingTerm
name|t
range|:
name|interesting
control|)
block|{
name|it
operator|.
name|add
argument_list|(
name|t
operator|.
name|term
operator|.
name|toString
argument_list|()
argument_list|,
name|t
operator|.
name|boost
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|add
argument_list|(
literal|"interestingTerms"
argument_list|,
name|it
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|List
argument_list|<
name|String
argument_list|>
name|it
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|interesting
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|InterestingTerm
name|t
range|:
name|interesting
control|)
block|{
name|it
operator|.
name|add
argument_list|(
name|t
operator|.
name|term
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|add
argument_list|(
literal|"interestingTerms"
argument_list|,
name|it
argument_list|)
expr_stmt|;
block|}
block|}
comment|// maybe facet the results
if|if
condition|(
name|params
operator|.
name|getBool
argument_list|(
name|FacetParams
operator|.
name|FACET
argument_list|,
literal|false
argument_list|)
condition|)
block|{
if|if
condition|(
name|mltDocs
operator|.
name|docSet
operator|==
literal|null
condition|)
block|{
name|rsp
operator|.
name|add
argument_list|(
literal|"facet_counts"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|SimpleFacets
name|f
init|=
operator|new
name|SimpleFacets
argument_list|(
name|req
argument_list|,
name|mltDocs
operator|.
name|docSet
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"facet_counts"
argument_list|,
name|f
operator|.
name|getFacetCounts
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Copied from StandardRequestHandler... perhaps it should be added to doStandardDebug?
try|try
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|dbg
init|=
name|SolrPluginUtils
operator|.
name|doStandardDebug
argument_list|(
name|req
argument_list|,
name|q
argument_list|,
name|mlt
operator|.
name|mltquery
argument_list|,
name|mltDocs
operator|.
name|docList
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|dbg
condition|)
block|{
if|if
condition|(
literal|null
operator|!=
name|filters
condition|)
block|{
name|dbg
operator|.
name|add
argument_list|(
literal|"filter_queries"
argument_list|,
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getParams
argument_list|(
name|CommonParams
operator|.
name|FQ
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fqs
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|filters
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Query
name|fq
range|:
name|filters
control|)
block|{
name|fqs
operator|.
name|add
argument_list|(
name|QueryParsing
operator|.
name|toString
argument_list|(
name|fq
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|dbg
operator|.
name|add
argument_list|(
literal|"parsed_filter_queries"
argument_list|,
name|fqs
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|add
argument_list|(
literal|"debug"
argument_list|,
name|dbg
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|logOnce
argument_list|(
name|SolrCore
operator|.
name|log
argument_list|,
literal|"Exception during debug"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"exception_during_debug"
argument_list|,
name|SolrException
operator|.
name|toStr
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|InterestingTerm
specifier|public
specifier|static
class|class
name|InterestingTerm
block|{
DECL|field|term
specifier|public
name|Term
name|term
decl_stmt|;
DECL|field|boost
specifier|public
name|float
name|boost
decl_stmt|;
DECL|field|BOOST_ORDER
specifier|public
specifier|static
name|Comparator
argument_list|<
name|InterestingTerm
argument_list|>
name|BOOST_ORDER
init|=
operator|new
name|Comparator
argument_list|<
name|InterestingTerm
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|InterestingTerm
name|t1
parameter_list|,
name|InterestingTerm
name|t2
parameter_list|)
block|{
name|float
name|d
init|=
name|t1
operator|.
name|boost
operator|-
name|t2
operator|.
name|boost
decl_stmt|;
if|if
condition|(
name|d
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
operator|(
name|d
operator|>
literal|0
operator|)
condition|?
literal|1
else|:
operator|-
literal|1
return|;
block|}
block|}
decl_stmt|;
block|}
comment|/**    * Helper class for MoreLikeThis that can be called from other request handlers    */
DECL|class|MoreLikeThisHelper
specifier|public
specifier|static
class|class
name|MoreLikeThisHelper
block|{
DECL|field|searcher
specifier|final
name|SolrIndexSearcher
name|searcher
decl_stmt|;
DECL|field|mlt
specifier|final
name|MoreLikeThis
name|mlt
decl_stmt|;
DECL|field|reader
specifier|final
name|IndexReader
name|reader
decl_stmt|;
DECL|field|uniqueKeyField
specifier|final
name|SchemaField
name|uniqueKeyField
decl_stmt|;
DECL|field|needDocSet
specifier|final
name|boolean
name|needDocSet
decl_stmt|;
DECL|field|boostFields
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|boostFields
decl_stmt|;
DECL|field|mltquery
name|Query
name|mltquery
decl_stmt|;
comment|// expose this for debugging
DECL|method|MoreLikeThisHelper
specifier|public
name|MoreLikeThisHelper
parameter_list|(
name|SolrParams
name|params
parameter_list|,
name|SolrIndexSearcher
name|searcher
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
name|reader
operator|=
name|searcher
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|this
operator|.
name|uniqueKeyField
operator|=
name|searcher
operator|.
name|getSchema
argument_list|()
operator|.
name|getUniqueKeyField
argument_list|()
expr_stmt|;
name|this
operator|.
name|needDocSet
operator|=
name|params
operator|.
name|getBool
argument_list|(
name|FacetParams
operator|.
name|FACET
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|SolrParams
name|required
init|=
name|params
operator|.
name|required
argument_list|()
decl_stmt|;
name|String
index|[]
name|fields
init|=
name|splitList
operator|.
name|split
argument_list|(
name|required
operator|.
name|get
argument_list|(
name|MoreLikeThisParams
operator|.
name|SIMILARITY_FIELDS
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|.
name|length
operator|<
literal|1
condition|)
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
literal|"MoreLikeThis requires at least one similarity field: "
operator|+
name|MoreLikeThisParams
operator|.
name|SIMILARITY_FIELDS
argument_list|)
throw|;
block|}
name|this
operator|.
name|mlt
operator|=
operator|new
name|MoreLikeThis
argument_list|(
name|reader
argument_list|)
expr_stmt|;
comment|// TODO -- after LUCENE-896, we can use , searcher.getSimilarity() );
name|mlt
operator|.
name|setFieldNames
argument_list|(
name|fields
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setAnalyzer
argument_list|(
name|searcher
operator|.
name|getSchema
argument_list|()
operator|.
name|getAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
comment|// configurable params
name|mlt
operator|.
name|setMinTermFreq
argument_list|(
name|params
operator|.
name|getInt
argument_list|(
name|MoreLikeThisParams
operator|.
name|MIN_TERM_FREQ
argument_list|,
name|MoreLikeThis
operator|.
name|DEFAULT_MIN_TERM_FREQ
argument_list|)
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMinDocFreq
argument_list|(
name|params
operator|.
name|getInt
argument_list|(
name|MoreLikeThisParams
operator|.
name|MIN_DOC_FREQ
argument_list|,
name|MoreLikeThis
operator|.
name|DEFAULT_MIN_DOC_FREQ
argument_list|)
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMinWordLen
argument_list|(
name|params
operator|.
name|getInt
argument_list|(
name|MoreLikeThisParams
operator|.
name|MIN_WORD_LEN
argument_list|,
name|MoreLikeThis
operator|.
name|DEFAULT_MIN_WORD_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMaxWordLen
argument_list|(
name|params
operator|.
name|getInt
argument_list|(
name|MoreLikeThisParams
operator|.
name|MAX_WORD_LEN
argument_list|,
name|MoreLikeThis
operator|.
name|DEFAULT_MAX_WORD_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMaxQueryTerms
argument_list|(
name|params
operator|.
name|getInt
argument_list|(
name|MoreLikeThisParams
operator|.
name|MAX_QUERY_TERMS
argument_list|,
name|MoreLikeThis
operator|.
name|DEFAULT_MAX_QUERY_TERMS
argument_list|)
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMaxNumTokensParsed
argument_list|(
name|params
operator|.
name|getInt
argument_list|(
name|MoreLikeThisParams
operator|.
name|MAX_NUM_TOKENS_PARSED
argument_list|,
name|MoreLikeThis
operator|.
name|DEFAULT_MAX_NUM_TOKENS_PARSED
argument_list|)
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setBoost
argument_list|(
name|params
operator|.
name|getBool
argument_list|(
name|MoreLikeThisParams
operator|.
name|BOOST
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|boostFields
operator|=
name|SolrPluginUtils
operator|.
name|parseFieldBoosts
argument_list|(
name|params
operator|.
name|getParams
argument_list|(
name|MoreLikeThisParams
operator|.
name|QF
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|setBoosts
specifier|private
name|void
name|setBoosts
parameter_list|(
name|Query
name|mltquery
parameter_list|)
block|{
if|if
condition|(
name|boostFields
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|List
name|clauses
init|=
operator|(
operator|(
name|BooleanQuery
operator|)
name|mltquery
operator|)
operator|.
name|clauses
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|clauses
control|)
block|{
name|TermQuery
name|q
init|=
call|(
name|TermQuery
call|)
argument_list|(
operator|(
name|BooleanClause
operator|)
name|o
argument_list|)
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|Float
name|b
init|=
name|this
operator|.
name|boostFields
operator|.
name|get
argument_list|(
name|q
operator|.
name|getTerm
argument_list|()
operator|.
name|field
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|b
operator|!=
literal|null
condition|)
block|{
name|q
operator|.
name|setBoost
argument_list|(
name|b
operator|*
name|q
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|getMoreLikeThis
specifier|public
name|DocListAndSet
name|getMoreLikeThis
parameter_list|(
name|int
name|id
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|rows
parameter_list|,
name|List
argument_list|<
name|Query
argument_list|>
name|filters
parameter_list|,
name|List
argument_list|<
name|InterestingTerm
argument_list|>
name|terms
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
name|Document
name|doc
init|=
name|reader
operator|.
name|document
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|mltquery
operator|=
name|mlt
operator|.
name|like
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|setBoosts
argument_list|(
name|mltquery
argument_list|)
expr_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|fillInterestingTermsFromMLTQuery
argument_list|(
name|mltquery
argument_list|,
name|terms
argument_list|)
expr_stmt|;
block|}
comment|// exclude current document from results
name|BooleanQuery
name|mltQuery
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|mltQuery
operator|.
name|add
argument_list|(
name|mltquery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|mltQuery
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|uniqueKeyField
operator|.
name|getName
argument_list|()
argument_list|,
name|doc
operator|.
name|get
argument_list|(
name|uniqueKeyField
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|DocListAndSet
name|results
init|=
operator|new
name|DocListAndSet
argument_list|()
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|needDocSet
condition|)
block|{
name|results
operator|=
name|searcher
operator|.
name|getDocListAndSet
argument_list|(
name|mltQuery
argument_list|,
name|filters
argument_list|,
literal|null
argument_list|,
name|start
argument_list|,
name|rows
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|results
operator|.
name|docList
operator|=
name|searcher
operator|.
name|getDocList
argument_list|(
name|mltQuery
argument_list|,
name|filters
argument_list|,
literal|null
argument_list|,
name|start
argument_list|,
name|rows
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
return|return
name|results
return|;
block|}
DECL|method|getMoreLikeThis
specifier|public
name|DocListAndSet
name|getMoreLikeThis
parameter_list|(
name|Reader
name|reader
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|rows
parameter_list|,
name|List
argument_list|<
name|Query
argument_list|>
name|filters
parameter_list|,
name|List
argument_list|<
name|InterestingTerm
argument_list|>
name|terms
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
name|mltquery
operator|=
name|mlt
operator|.
name|like
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|setBoosts
argument_list|(
name|mltquery
argument_list|)
expr_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|fillInterestingTermsFromMLTQuery
argument_list|(
name|mltquery
argument_list|,
name|terms
argument_list|)
expr_stmt|;
block|}
name|DocListAndSet
name|results
init|=
operator|new
name|DocListAndSet
argument_list|()
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|needDocSet
condition|)
block|{
name|results
operator|=
name|searcher
operator|.
name|getDocListAndSet
argument_list|(
name|mltquery
argument_list|,
name|filters
argument_list|,
literal|null
argument_list|,
name|start
argument_list|,
name|rows
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|results
operator|.
name|docList
operator|=
name|searcher
operator|.
name|getDocList
argument_list|(
name|mltquery
argument_list|,
name|filters
argument_list|,
literal|null
argument_list|,
name|start
argument_list|,
name|rows
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
return|return
name|results
return|;
block|}
DECL|method|getMoreLikeThese
specifier|public
name|NamedList
argument_list|<
name|DocList
argument_list|>
name|getMoreLikeThese
parameter_list|(
name|DocList
name|docs
parameter_list|,
name|int
name|rows
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexSchema
name|schema
init|=
name|searcher
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|DocList
argument_list|>
name|mlt
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|DocList
argument_list|>
argument_list|()
decl_stmt|;
name|DocIterator
name|iterator
init|=
name|docs
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|int
name|id
init|=
name|iterator
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|DocListAndSet
name|sim
init|=
name|getMoreLikeThis
argument_list|(
name|id
argument_list|,
literal|0
argument_list|,
name|rows
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|flags
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|schema
operator|.
name|printableUniqueKey
argument_list|(
name|reader
operator|.
name|document
argument_list|(
name|id
argument_list|)
argument_list|)
decl_stmt|;
name|mlt
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|sim
operator|.
name|docList
argument_list|)
expr_stmt|;
block|}
return|return
name|mlt
return|;
block|}
DECL|method|fillInterestingTermsFromMLTQuery
specifier|private
name|void
name|fillInterestingTermsFromMLTQuery
parameter_list|(
name|Query
name|query
parameter_list|,
name|List
argument_list|<
name|InterestingTerm
argument_list|>
name|terms
parameter_list|)
block|{
name|List
name|clauses
init|=
operator|(
operator|(
name|BooleanQuery
operator|)
name|mltquery
operator|)
operator|.
name|clauses
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|clauses
control|)
block|{
name|TermQuery
name|q
init|=
call|(
name|TermQuery
call|)
argument_list|(
operator|(
name|BooleanClause
operator|)
name|o
argument_list|)
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|InterestingTerm
name|it
init|=
operator|new
name|InterestingTerm
argument_list|()
decl_stmt|;
name|it
operator|.
name|boost
operator|=
name|q
operator|.
name|getBoost
argument_list|()
expr_stmt|;
name|it
operator|.
name|term
operator|=
name|q
operator|.
name|getTerm
argument_list|()
expr_stmt|;
name|terms
operator|.
name|add
argument_list|(
name|it
argument_list|)
expr_stmt|;
block|}
comment|// alternatively we could use
comment|// mltquery.extractTerms( terms );
block|}
DECL|method|getMoreLikeThis
specifier|public
name|MoreLikeThis
name|getMoreLikeThis
parameter_list|()
block|{
return|return
name|mlt
return|;
block|}
block|}
comment|//////////////////////// SolrInfoMBeans methods //////////////////////
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|"$Revision$"
return|;
block|}
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Solr MoreLikeThis"
return|;
block|}
annotation|@
name|Override
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"$Id$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL$"
return|;
block|}
annotation|@
name|Override
DECL|method|getDocs
specifier|public
name|URL
index|[]
name|getDocs
parameter_list|()
block|{
try|try
block|{
return|return
operator|new
name|URL
index|[]
block|{
operator|new
name|URL
argument_list|(
literal|"http://wiki.apache.org/solr/MoreLikeThis"
argument_list|)
block|}
return|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|ex
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class
end_unit
