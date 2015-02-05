begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|IntIntOpenHashMap
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
name|Explanation
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
name|search
operator|.
name|QueryRescorer
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
name|TopDocsCollector
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
name|TopFieldCollector
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
name|TopScoreDocCollector
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
name|handler
operator|.
name|component
operator|.
name|MergeStrategy
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
name|QueryElevationComponent
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
name|lucene
operator|.
name|search
operator|.
name|LeafCollector
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
name|Weight
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
name|IndexSearcher
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
name|TopDocs
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
name|Scorer
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
name|Sort
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
name|search
operator|.
name|ScoreDoc
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|IntFloatOpenHashMap
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
name|Arrays
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
comment|/* * *  Syntax: q=*:*&rq={!rerank reRankQuery=$rqq reRankDocs=300 reRankWeight=3} * */
end_comment
begin_class
DECL|class|ReRankQParserPlugin
specifier|public
class|class
name|ReRankQParserPlugin
extends|extends
name|QParserPlugin
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"rerank"
decl_stmt|;
DECL|field|defaultQuery
specifier|private
specifier|static
name|Query
name|defaultQuery
init|=
operator|new
name|MatchAllDocsQuery
argument_list|()
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{   }
DECL|method|createParser
specifier|public
name|QParser
name|createParser
parameter_list|(
name|String
name|query
parameter_list|,
name|SolrParams
name|localParams
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
return|return
operator|new
name|ReRankQParser
argument_list|(
name|query
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
return|;
block|}
DECL|class|ReRankQParser
specifier|private
class|class
name|ReRankQParser
extends|extends
name|QParser
block|{
DECL|method|ReRankQParser
specifier|public
name|ReRankQParser
parameter_list|(
name|String
name|query
parameter_list|,
name|SolrParams
name|localParams
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|super
argument_list|(
name|query
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|()
throws|throws
name|SyntaxError
block|{
name|String
name|reRankQueryString
init|=
name|localParams
operator|.
name|get
argument_list|(
literal|"reRankQuery"
argument_list|)
decl_stmt|;
name|QParser
name|reRankParser
init|=
name|QParser
operator|.
name|getParser
argument_list|(
name|reRankQueryString
argument_list|,
literal|null
argument_list|,
name|req
argument_list|)
decl_stmt|;
name|Query
name|reRankQuery
init|=
name|reRankParser
operator|.
name|parse
argument_list|()
decl_stmt|;
name|int
name|reRankDocs
init|=
name|localParams
operator|.
name|getInt
argument_list|(
literal|"reRankDocs"
argument_list|,
literal|200
argument_list|)
decl_stmt|;
name|reRankDocs
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|reRankDocs
argument_list|)
expr_stmt|;
comment|//
name|double
name|reRankWeight
init|=
name|localParams
operator|.
name|getDouble
argument_list|(
literal|"reRankWeight"
argument_list|,
literal|2.0d
argument_list|)
decl_stmt|;
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
name|int
name|length
init|=
name|start
operator|+
name|rows
decl_stmt|;
return|return
operator|new
name|ReRankQuery
argument_list|(
name|reRankQuery
argument_list|,
name|reRankDocs
argument_list|,
name|reRankWeight
argument_list|,
name|length
argument_list|)
return|;
block|}
block|}
DECL|class|ReRankQuery
specifier|private
class|class
name|ReRankQuery
extends|extends
name|RankQuery
block|{
DECL|field|mainQuery
specifier|private
name|Query
name|mainQuery
init|=
name|defaultQuery
decl_stmt|;
DECL|field|reRankQuery
specifier|private
name|Query
name|reRankQuery
decl_stmt|;
DECL|field|reRankDocs
specifier|private
name|int
name|reRankDocs
decl_stmt|;
DECL|field|length
specifier|private
name|int
name|length
decl_stmt|;
DECL|field|reRankWeight
specifier|private
name|double
name|reRankWeight
decl_stmt|;
DECL|field|boostedPriority
specifier|private
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|Integer
argument_list|>
name|boostedPriority
decl_stmt|;
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|mainQuery
operator|.
name|hashCode
argument_list|()
operator|+
name|reRankQuery
operator|.
name|hashCode
argument_list|()
operator|+
operator|(
name|int
operator|)
name|reRankWeight
operator|+
name|reRankDocs
operator|+
operator|(
name|int
operator|)
name|getBoost
argument_list|()
return|;
block|}
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|ReRankQuery
condition|)
block|{
name|ReRankQuery
name|rrq
init|=
operator|(
name|ReRankQuery
operator|)
name|o
decl_stmt|;
return|return
operator|(
name|mainQuery
operator|.
name|equals
argument_list|(
name|rrq
operator|.
name|mainQuery
argument_list|)
operator|&&
name|reRankQuery
operator|.
name|equals
argument_list|(
name|rrq
operator|.
name|reRankQuery
argument_list|)
operator|&&
name|reRankWeight
operator|==
name|rrq
operator|.
name|reRankWeight
operator|&&
name|reRankDocs
operator|==
name|rrq
operator|.
name|reRankDocs
operator|&&
name|getBoost
argument_list|()
operator|==
name|rrq
operator|.
name|getBoost
argument_list|()
operator|)
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|ReRankQuery
specifier|public
name|ReRankQuery
parameter_list|(
name|Query
name|reRankQuery
parameter_list|,
name|int
name|reRankDocs
parameter_list|,
name|double
name|reRankWeight
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|reRankQuery
operator|=
name|reRankQuery
expr_stmt|;
name|this
operator|.
name|reRankDocs
operator|=
name|reRankDocs
expr_stmt|;
name|this
operator|.
name|reRankWeight
operator|=
name|reRankWeight
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
DECL|method|wrap
specifier|public
name|RankQuery
name|wrap
parameter_list|(
name|Query
name|_mainQuery
parameter_list|)
block|{
if|if
condition|(
name|_mainQuery
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|mainQuery
operator|=
name|_mainQuery
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|getMergeStrategy
specifier|public
name|MergeStrategy
name|getMergeStrategy
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|getTopDocsCollector
specifier|public
name|TopDocsCollector
name|getTopDocsCollector
parameter_list|(
name|int
name|len
parameter_list|,
name|SolrIndexSearcher
operator|.
name|QueryCommand
name|cmd
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|this
operator|.
name|boostedPriority
operator|==
literal|null
condition|)
block|{
name|SolrRequestInfo
name|info
init|=
name|SolrRequestInfo
operator|.
name|getRequestInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|Map
name|context
init|=
name|info
operator|.
name|getReq
argument_list|()
operator|.
name|getContext
argument_list|()
decl_stmt|;
name|this
operator|.
name|boostedPriority
operator|=
operator|(
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|Integer
argument_list|>
operator|)
name|context
operator|.
name|get
argument_list|(
name|QueryElevationComponent
operator|.
name|BOOSTED_PRIORITY
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|ReRankCollector
argument_list|(
name|reRankDocs
argument_list|,
name|length
argument_list|,
name|reRankQuery
argument_list|,
name|reRankWeight
argument_list|,
name|cmd
argument_list|,
name|searcher
argument_list|,
name|boostedPriority
argument_list|)
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
literal|"{!rerank mainQuery='"
operator|+
name|mainQuery
operator|.
name|toString
argument_list|()
operator|+
literal|"' reRankQuery='"
operator|+
name|reRankQuery
operator|.
name|toString
argument_list|()
operator|+
literal|"' reRankDocs="
operator|+
name|reRankDocs
operator|+
literal|" reRankWeigh="
operator|+
name|reRankWeight
operator|+
literal|"}"
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|toString
argument_list|(
literal|null
argument_list|)
return|;
block|}
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|wrap
argument_list|(
name|this
operator|.
name|mainQuery
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
argument_list|)
return|;
block|}
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
name|this
operator|.
name|mainQuery
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ReRankWeight
argument_list|(
name|mainQuery
argument_list|,
name|reRankQuery
argument_list|,
name|reRankWeight
argument_list|,
name|searcher
argument_list|)
return|;
block|}
block|}
DECL|class|ReRankWeight
specifier|private
class|class
name|ReRankWeight
extends|extends
name|Weight
block|{
DECL|field|reRankQuery
specifier|private
name|Query
name|reRankQuery
decl_stmt|;
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|mainWeight
specifier|private
name|Weight
name|mainWeight
decl_stmt|;
DECL|field|reRankWeight
specifier|private
name|double
name|reRankWeight
decl_stmt|;
DECL|method|ReRankWeight
specifier|public
name|ReRankWeight
parameter_list|(
name|Query
name|mainQuery
parameter_list|,
name|Query
name|reRankQuery
parameter_list|,
name|double
name|reRankWeight
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|reRankQuery
operator|=
name|reRankQuery
expr_stmt|;
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
name|this
operator|.
name|reRankWeight
operator|=
name|reRankWeight
expr_stmt|;
name|this
operator|.
name|mainWeight
operator|=
name|mainQuery
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
block|}
DECL|method|getValueForNormalization
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|mainWeight
operator|.
name|getValueForNormalization
argument_list|()
return|;
block|}
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|bits
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|mainWeight
operator|.
name|scorer
argument_list|(
name|context
argument_list|,
name|bits
argument_list|,
name|needsScores
argument_list|)
return|;
block|}
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|mainWeight
operator|.
name|getQuery
argument_list|()
return|;
block|}
DECL|method|normalize
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|,
name|float
name|topLevelBoost
parameter_list|)
block|{
name|mainWeight
operator|.
name|normalize
argument_list|(
name|norm
argument_list|,
name|topLevelBoost
argument_list|)
expr_stmt|;
block|}
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|Explanation
name|mainExplain
init|=
name|mainWeight
operator|.
name|explain
argument_list|(
name|context
argument_list|,
name|doc
argument_list|)
decl_stmt|;
return|return
operator|new
name|QueryRescorer
argument_list|(
name|reRankQuery
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|float
name|combine
parameter_list|(
name|float
name|firstPassScore
parameter_list|,
name|boolean
name|secondPassMatches
parameter_list|,
name|float
name|secondPassScore
parameter_list|)
block|{
name|float
name|score
init|=
name|firstPassScore
decl_stmt|;
if|if
condition|(
name|secondPassMatches
condition|)
block|{
name|score
operator|+=
name|reRankWeight
operator|*
name|secondPassScore
expr_stmt|;
block|}
return|return
name|score
return|;
block|}
block|}
operator|.
name|explain
argument_list|(
name|searcher
argument_list|,
name|mainExplain
argument_list|,
name|context
operator|.
name|docBase
operator|+
name|doc
argument_list|)
return|;
block|}
block|}
DECL|class|ReRankCollector
specifier|private
class|class
name|ReRankCollector
extends|extends
name|TopDocsCollector
block|{
DECL|field|reRankQuery
specifier|private
name|Query
name|reRankQuery
decl_stmt|;
DECL|field|mainCollector
specifier|private
name|TopDocsCollector
name|mainCollector
decl_stmt|;
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|reRankDocs
specifier|private
name|int
name|reRankDocs
decl_stmt|;
DECL|field|length
specifier|private
name|int
name|length
decl_stmt|;
DECL|field|reRankWeight
specifier|private
name|double
name|reRankWeight
decl_stmt|;
DECL|field|boostedPriority
specifier|private
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|Integer
argument_list|>
name|boostedPriority
decl_stmt|;
DECL|method|ReRankCollector
specifier|public
name|ReRankCollector
parameter_list|(
name|int
name|reRankDocs
parameter_list|,
name|int
name|length
parameter_list|,
name|Query
name|reRankQuery
parameter_list|,
name|double
name|reRankWeight
parameter_list|,
name|SolrIndexSearcher
operator|.
name|QueryCommand
name|cmd
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|,
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|Integer
argument_list|>
name|boostedPriority
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|reRankQuery
operator|=
name|reRankQuery
expr_stmt|;
name|this
operator|.
name|reRankDocs
operator|=
name|reRankDocs
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|boostedPriority
operator|=
name|boostedPriority
expr_stmt|;
name|Sort
name|sort
init|=
name|cmd
operator|.
name|getSort
argument_list|()
decl_stmt|;
if|if
condition|(
name|sort
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|mainCollector
operator|=
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|this
operator|.
name|reRankDocs
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sort
operator|=
name|sort
operator|.
name|rewrite
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
name|this
operator|.
name|mainCollector
operator|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
argument_list|,
name|Math
operator|.
name|max
argument_list|(
name|this
operator|.
name|reRankDocs
argument_list|,
name|length
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
name|this
operator|.
name|reRankWeight
operator|=
name|reRankWeight
expr_stmt|;
block|}
DECL|method|getTotalHits
specifier|public
name|int
name|getTotalHits
parameter_list|()
block|{
return|return
name|mainCollector
operator|.
name|getTotalHits
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getLeafCollector
specifier|public
name|LeafCollector
name|getLeafCollector
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|mainCollector
operator|.
name|getLeafCollector
argument_list|(
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|method|topDocs
specifier|public
name|TopDocs
name|topDocs
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|howMany
parameter_list|)
block|{
try|try
block|{
name|TopDocs
name|mainDocs
init|=
name|mainCollector
operator|.
name|topDocs
argument_list|(
literal|0
argument_list|,
name|Math
operator|.
name|max
argument_list|(
name|reRankDocs
argument_list|,
name|length
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|mainDocs
operator|.
name|totalHits
operator|==
literal|0
operator|||
name|mainDocs
operator|.
name|scoreDocs
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|mainDocs
return|;
block|}
if|if
condition|(
name|boostedPriority
operator|!=
literal|null
condition|)
block|{
name|SolrRequestInfo
name|info
init|=
name|SolrRequestInfo
operator|.
name|getRequestInfo
argument_list|()
decl_stmt|;
name|Map
name|requestContext
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|requestContext
operator|=
name|info
operator|.
name|getReq
argument_list|()
operator|.
name|getContext
argument_list|()
expr_stmt|;
block|}
name|IntIntOpenHashMap
name|boostedDocs
init|=
name|QueryElevationComponent
operator|.
name|getBoostDocs
argument_list|(
operator|(
name|SolrIndexSearcher
operator|)
name|searcher
argument_list|,
name|boostedPriority
argument_list|,
name|requestContext
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|mainScoreDocs
init|=
name|mainDocs
operator|.
name|scoreDocs
decl_stmt|;
name|ScoreDoc
index|[]
name|reRankScoreDocs
init|=
operator|new
name|ScoreDoc
index|[
name|Math
operator|.
name|min
argument_list|(
name|mainScoreDocs
operator|.
name|length
argument_list|,
name|reRankDocs
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|mainScoreDocs
argument_list|,
literal|0
argument_list|,
name|reRankScoreDocs
argument_list|,
literal|0
argument_list|,
name|reRankScoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
name|mainDocs
operator|.
name|scoreDocs
operator|=
name|reRankScoreDocs
expr_stmt|;
name|TopDocs
name|rescoredDocs
init|=
operator|new
name|QueryRescorer
argument_list|(
name|reRankQuery
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|float
name|combine
parameter_list|(
name|float
name|firstPassScore
parameter_list|,
name|boolean
name|secondPassMatches
parameter_list|,
name|float
name|secondPassScore
parameter_list|)
block|{
name|float
name|score
init|=
name|firstPassScore
decl_stmt|;
if|if
condition|(
name|secondPassMatches
condition|)
block|{
name|score
operator|+=
name|reRankWeight
operator|*
name|secondPassScore
expr_stmt|;
block|}
return|return
name|score
return|;
block|}
block|}
operator|.
name|rescore
argument_list|(
name|searcher
argument_list|,
name|mainDocs
argument_list|,
name|mainDocs
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|rescoredDocs
operator|.
name|scoreDocs
argument_list|,
operator|new
name|BoostedComp
argument_list|(
name|boostedDocs
argument_list|,
name|mainDocs
operator|.
name|scoreDocs
argument_list|,
name|rescoredDocs
operator|.
name|getMaxScore
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|//Lower howMany if we've collected fewer documents.
name|howMany
operator|=
name|Math
operator|.
name|min
argument_list|(
name|howMany
argument_list|,
name|mainScoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|howMany
operator|==
name|rescoredDocs
operator|.
name|scoreDocs
operator|.
name|length
condition|)
block|{
return|return
name|rescoredDocs
return|;
comment|// Just return the rescoredDocs
block|}
elseif|else
if|if
condition|(
name|howMany
operator|>
name|rescoredDocs
operator|.
name|scoreDocs
operator|.
name|length
condition|)
block|{
comment|//We need to return more then we've reRanked, so create the combined page.
name|ScoreDoc
index|[]
name|scoreDocs
init|=
operator|new
name|ScoreDoc
index|[
name|howMany
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|mainScoreDocs
argument_list|,
literal|0
argument_list|,
name|scoreDocs
argument_list|,
literal|0
argument_list|,
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
comment|//lay down the initial docs
name|System
operator|.
name|arraycopy
argument_list|(
name|rescoredDocs
operator|.
name|scoreDocs
argument_list|,
literal|0
argument_list|,
name|scoreDocs
argument_list|,
literal|0
argument_list|,
name|rescoredDocs
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
comment|//overlay the re-ranked docs.
name|rescoredDocs
operator|.
name|scoreDocs
operator|=
name|scoreDocs
expr_stmt|;
return|return
name|rescoredDocs
return|;
block|}
else|else
block|{
comment|//We've rescored more then we need to return.
name|ScoreDoc
index|[]
name|scoreDocs
init|=
operator|new
name|ScoreDoc
index|[
name|howMany
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|rescoredDocs
operator|.
name|scoreDocs
argument_list|,
literal|0
argument_list|,
name|scoreDocs
argument_list|,
literal|0
argument_list|,
name|howMany
argument_list|)
expr_stmt|;
name|rescoredDocs
operator|.
name|scoreDocs
operator|=
name|scoreDocs
expr_stmt|;
return|return
name|rescoredDocs
return|;
block|}
block|}
else|else
block|{
name|ScoreDoc
index|[]
name|mainScoreDocs
init|=
name|mainDocs
operator|.
name|scoreDocs
decl_stmt|;
comment|/*           *  Create the array for the reRankScoreDocs.           */
name|ScoreDoc
index|[]
name|reRankScoreDocs
init|=
operator|new
name|ScoreDoc
index|[
name|Math
operator|.
name|min
argument_list|(
name|mainScoreDocs
operator|.
name|length
argument_list|,
name|reRankDocs
argument_list|)
index|]
decl_stmt|;
comment|/*           *  Copy the initial results into the reRankScoreDocs array.           */
name|System
operator|.
name|arraycopy
argument_list|(
name|mainScoreDocs
argument_list|,
literal|0
argument_list|,
name|reRankScoreDocs
argument_list|,
literal|0
argument_list|,
name|reRankScoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
name|mainDocs
operator|.
name|scoreDocs
operator|=
name|reRankScoreDocs
expr_stmt|;
name|TopDocs
name|rescoredDocs
init|=
operator|new
name|QueryRescorer
argument_list|(
name|reRankQuery
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|float
name|combine
parameter_list|(
name|float
name|firstPassScore
parameter_list|,
name|boolean
name|secondPassMatches
parameter_list|,
name|float
name|secondPassScore
parameter_list|)
block|{
name|float
name|score
init|=
name|firstPassScore
decl_stmt|;
if|if
condition|(
name|secondPassMatches
condition|)
block|{
name|score
operator|+=
name|reRankWeight
operator|*
name|secondPassScore
expr_stmt|;
block|}
return|return
name|score
return|;
block|}
block|}
operator|.
name|rescore
argument_list|(
name|searcher
argument_list|,
name|mainDocs
argument_list|,
name|mainDocs
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
decl_stmt|;
comment|//Lower howMany to return if we've collected fewer documents.
name|howMany
operator|=
name|Math
operator|.
name|min
argument_list|(
name|howMany
argument_list|,
name|mainScoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|howMany
operator|==
name|rescoredDocs
operator|.
name|scoreDocs
operator|.
name|length
condition|)
block|{
return|return
name|rescoredDocs
return|;
comment|// Just return the rescoredDocs
block|}
elseif|else
if|if
condition|(
name|howMany
operator|>
name|rescoredDocs
operator|.
name|scoreDocs
operator|.
name|length
condition|)
block|{
comment|//We need to return more then we've reRanked, so create the combined page.
name|ScoreDoc
index|[]
name|scoreDocs
init|=
operator|new
name|ScoreDoc
index|[
name|howMany
index|]
decl_stmt|;
comment|//lay down the initial docs
name|System
operator|.
name|arraycopy
argument_list|(
name|mainScoreDocs
argument_list|,
literal|0
argument_list|,
name|scoreDocs
argument_list|,
literal|0
argument_list|,
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
comment|//overlay the rescoreds docs
name|System
operator|.
name|arraycopy
argument_list|(
name|rescoredDocs
operator|.
name|scoreDocs
argument_list|,
literal|0
argument_list|,
name|scoreDocs
argument_list|,
literal|0
argument_list|,
name|rescoredDocs
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
name|rescoredDocs
operator|.
name|scoreDocs
operator|=
name|scoreDocs
expr_stmt|;
return|return
name|rescoredDocs
return|;
block|}
else|else
block|{
comment|//We've rescored more then we need to return.
name|ScoreDoc
index|[]
name|scoreDocs
init|=
operator|new
name|ScoreDoc
index|[
name|howMany
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|rescoredDocs
operator|.
name|scoreDocs
argument_list|,
literal|0
argument_list|,
name|scoreDocs
argument_list|,
literal|0
argument_list|,
name|howMany
argument_list|)
expr_stmt|;
name|rescoredDocs
operator|.
name|scoreDocs
operator|=
name|scoreDocs
expr_stmt|;
return|return
name|rescoredDocs
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
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
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|class|BoostedComp
specifier|public
class|class
name|BoostedComp
implements|implements
name|Comparator
block|{
DECL|field|boostedMap
name|IntFloatOpenHashMap
name|boostedMap
decl_stmt|;
DECL|method|BoostedComp
specifier|public
name|BoostedComp
parameter_list|(
name|IntIntOpenHashMap
name|boostedDocs
parameter_list|,
name|ScoreDoc
index|[]
name|scoreDocs
parameter_list|,
name|float
name|maxScore
parameter_list|)
block|{
name|this
operator|.
name|boostedMap
operator|=
operator|new
name|IntFloatOpenHashMap
argument_list|(
name|boostedDocs
operator|.
name|size
argument_list|()
operator|*
literal|2
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|scoreDocs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|boostedDocs
operator|.
name|containsKey
argument_list|(
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
condition|)
block|{
name|boostedMap
operator|.
name|put
argument_list|(
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
argument_list|,
name|maxScore
operator|+
name|boostedDocs
operator|.
name|lget
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
name|ScoreDoc
name|doc1
init|=
operator|(
name|ScoreDoc
operator|)
name|o1
decl_stmt|;
name|ScoreDoc
name|doc2
init|=
operator|(
name|ScoreDoc
operator|)
name|o2
decl_stmt|;
name|float
name|score1
init|=
name|doc1
operator|.
name|score
decl_stmt|;
name|float
name|score2
init|=
name|doc2
operator|.
name|score
decl_stmt|;
if|if
condition|(
name|boostedMap
operator|.
name|containsKey
argument_list|(
name|doc1
operator|.
name|doc
argument_list|)
condition|)
block|{
name|score1
operator|=
name|boostedMap
operator|.
name|lget
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|boostedMap
operator|.
name|containsKey
argument_list|(
name|doc2
operator|.
name|doc
argument_list|)
condition|)
block|{
name|score2
operator|=
name|boostedMap
operator|.
name|lget
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|score1
operator|>
name|score2
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|score1
operator|<
name|score2
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
block|}
block|}
end_class
end_unit
