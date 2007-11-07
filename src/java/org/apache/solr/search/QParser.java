begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|ParseException
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
name|request
operator|.
name|SolrQueryRequest
import|;
end_import
begin_class
DECL|class|QParser
specifier|public
specifier|abstract
class|class
name|QParser
block|{
DECL|field|qstr
name|String
name|qstr
decl_stmt|;
DECL|field|params
name|SolrParams
name|params
decl_stmt|;
DECL|field|localParams
name|SolrParams
name|localParams
decl_stmt|;
DECL|field|req
name|SolrQueryRequest
name|req
decl_stmt|;
DECL|field|recurseCount
name|int
name|recurseCount
decl_stmt|;
DECL|field|query
name|Query
name|query
decl_stmt|;
DECL|method|QParser
specifier|public
name|QParser
parameter_list|(
name|String
name|qstr
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
name|this
operator|.
name|qstr
operator|=
name|qstr
expr_stmt|;
name|this
operator|.
name|localParams
operator|=
name|localParams
expr_stmt|;
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
name|this
operator|.
name|req
operator|=
name|req
expr_stmt|;
block|}
comment|/** create and return the<code>Query</code> object represented by<code>qstr</code> */
DECL|method|parse
specifier|protected
specifier|abstract
name|Query
name|parse
parameter_list|()
throws|throws
name|ParseException
function_decl|;
DECL|method|getLocalParams
specifier|public
name|SolrParams
name|getLocalParams
parameter_list|()
block|{
return|return
name|localParams
return|;
block|}
DECL|method|setLocalParams
specifier|public
name|void
name|setLocalParams
parameter_list|(
name|SolrParams
name|localParams
parameter_list|)
block|{
name|this
operator|.
name|localParams
operator|=
name|localParams
expr_stmt|;
block|}
DECL|method|getParams
specifier|public
name|SolrParams
name|getParams
parameter_list|()
block|{
return|return
name|params
return|;
block|}
DECL|method|setParams
specifier|public
name|void
name|setParams
parameter_list|(
name|SolrParams
name|params
parameter_list|)
block|{
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
block|}
DECL|method|getReq
specifier|public
name|SolrQueryRequest
name|getReq
parameter_list|()
block|{
return|return
name|req
return|;
block|}
DECL|method|setReq
specifier|public
name|void
name|setReq
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|this
operator|.
name|req
operator|=
name|req
expr_stmt|;
block|}
DECL|method|getString
specifier|public
name|String
name|getString
parameter_list|()
block|{
return|return
name|qstr
return|;
block|}
DECL|method|setString
specifier|public
name|void
name|setString
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|this
operator|.
name|qstr
operator|=
name|s
expr_stmt|;
block|}
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
throws|throws
name|ParseException
block|{
if|if
condition|(
name|query
operator|==
literal|null
condition|)
block|{
name|query
operator|=
name|parse
argument_list|()
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
DECL|method|checkRecurse
specifier|private
name|void
name|checkRecurse
parameter_list|()
throws|throws
name|ParseException
block|{
if|if
condition|(
name|recurseCount
operator|++
operator|>=
literal|100
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Infinite Recursion detected parsing query '"
operator|+
name|qstr
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
comment|// TODO: replace with a SolrParams that defaults to checking localParams first?
comment|// ideas..
comment|//   create params that satisfy field-specific overrides
comment|//   overrideable syntax $x=foo  (set global for limited scope) (invariants& security?)
comment|//                       $x+=foo (append to global for limited scope)
comment|/** check both local and global params */
DECL|method|getParam
specifier|protected
name|String
name|getParam
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|String
name|val
decl_stmt|;
if|if
condition|(
name|localParams
operator|!=
literal|null
condition|)
block|{
name|val
operator|=
name|localParams
operator|.
name|get
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
return|return
name|val
return|;
block|}
return|return
name|params
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/** Create a new QParser for parsing an embedded sub-query */
DECL|method|subQuery
specifier|public
name|QParser
name|subQuery
parameter_list|(
name|String
name|q
parameter_list|,
name|String
name|defaultType
parameter_list|)
throws|throws
name|ParseException
block|{
name|checkRecurse
argument_list|()
expr_stmt|;
if|if
condition|(
name|defaultType
operator|==
literal|null
operator|&&
name|localParams
operator|!=
literal|null
condition|)
block|{
comment|// if not passed, try and get the defaultType from local params
name|defaultType
operator|=
name|localParams
operator|.
name|get
argument_list|(
name|QueryParsing
operator|.
name|DEFTYPE
argument_list|)
expr_stmt|;
block|}
name|QParser
name|nestedParser
init|=
name|getParser
argument_list|(
name|q
argument_list|,
name|defaultType
argument_list|,
name|getReq
argument_list|()
argument_list|)
decl_stmt|;
name|nestedParser
operator|.
name|recurseCount
operator|=
name|recurseCount
expr_stmt|;
return|return
name|nestedParser
return|;
block|}
comment|/**    * @param useGlobalParams look up sort, start, rows in global params if not in local params    * @return the sort specification    */
DECL|method|getSort
specifier|public
name|QueryParsing
operator|.
name|SortSpec
name|getSort
parameter_list|(
name|boolean
name|useGlobalParams
parameter_list|)
throws|throws
name|ParseException
block|{
name|getQuery
argument_list|()
expr_stmt|;
comment|// ensure query is parsed first
name|String
name|sortStr
init|=
literal|null
decl_stmt|;
name|String
name|startS
init|=
literal|null
decl_stmt|;
name|String
name|rowsS
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|localParams
operator|!=
literal|null
condition|)
block|{
name|sortStr
operator|=
name|localParams
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|SORT
argument_list|)
expr_stmt|;
name|startS
operator|=
name|localParams
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|START
argument_list|)
expr_stmt|;
name|rowsS
operator|=
name|localParams
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|ROWS
argument_list|)
expr_stmt|;
comment|// if any of these parameters are present, don't go back to the global params
if|if
condition|(
name|sortStr
operator|!=
literal|null
operator|||
name|startS
operator|!=
literal|null
operator|||
name|rowsS
operator|!=
literal|null
condition|)
block|{
name|useGlobalParams
operator|=
literal|false
expr_stmt|;
block|}
block|}
if|if
condition|(
name|useGlobalParams
condition|)
block|{
if|if
condition|(
name|sortStr
operator|==
literal|null
condition|)
block|{
name|sortStr
operator|=
name|params
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|SORT
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|startS
operator|==
literal|null
condition|)
block|{
name|startS
operator|=
name|params
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|START
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rowsS
operator|==
literal|null
condition|)
block|{
name|rowsS
operator|=
name|params
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|ROWS
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|start
init|=
name|startS
operator|!=
literal|null
condition|?
name|Integer
operator|.
name|parseInt
argument_list|(
name|startS
argument_list|)
else|:
literal|0
decl_stmt|;
name|int
name|rows
init|=
name|rowsS
operator|!=
literal|null
condition|?
name|Integer
operator|.
name|parseInt
argument_list|(
name|rowsS
argument_list|)
else|:
literal|10
decl_stmt|;
name|QueryParsing
operator|.
name|SortSpec
name|sort
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|sortStr
operator|!=
literal|null
condition|)
block|{
comment|// may return null if 'score desc'
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
name|sortStr
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sort
operator|==
literal|null
condition|)
block|{
name|sort
operator|=
operator|new
name|QueryParsing
operator|.
name|SortSpec
argument_list|(
literal|null
argument_list|,
name|start
argument_list|,
name|rows
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sort
operator|.
name|offset
operator|=
name|start
expr_stmt|;
name|sort
operator|.
name|num
operator|=
name|rows
expr_stmt|;
block|}
return|return
name|sort
return|;
block|}
DECL|method|getDefaultHighlightFields
specifier|public
name|String
index|[]
name|getDefaultHighlightFields
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{}
return|;
block|}
DECL|method|getHighlightQuery
specifier|public
name|Query
name|getHighlightQuery
parameter_list|()
throws|throws
name|ParseException
block|{
return|return
name|getQuery
argument_list|()
return|;
block|}
comment|/** Create a<code>QParser</code> to parse<code>qstr</code>,    * assuming that the default query type is<code>defaultType</code>.    * The query type may be overridden by local parameters in the query    * string itself.  For example if defaultType=<code>"dismax"</code>    * and qstr=<code>foo</code>, then the dismax query parser will be used    * to parse and construct the query object.  However    * if qstr=<code>&lt;!prefix f=myfield&gt;foo</code>    * then the prefix query parser will be used.    */
DECL|method|getParser
specifier|public
specifier|static
name|QParser
name|getParser
parameter_list|(
name|String
name|qstr
parameter_list|,
name|String
name|defaultType
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
throws|throws
name|ParseException
block|{
name|SolrParams
name|localParams
init|=
name|QueryParsing
operator|.
name|getLocalParams
argument_list|(
name|qstr
argument_list|,
name|req
operator|.
name|getParams
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|type
decl_stmt|;
if|if
condition|(
name|localParams
operator|==
literal|null
condition|)
block|{
name|type
operator|=
name|defaultType
expr_stmt|;
block|}
else|else
block|{
name|type
operator|=
name|localParams
operator|.
name|get
argument_list|(
name|QueryParsing
operator|.
name|TYPE
argument_list|)
expr_stmt|;
name|qstr
operator|=
name|localParams
operator|.
name|get
argument_list|(
literal|"v"
argument_list|)
expr_stmt|;
block|}
name|type
operator|=
name|type
operator|==
literal|null
condition|?
name|QParserPlugin
operator|.
name|DEFAULT_QTYPE
else|:
name|type
expr_stmt|;
name|QParserPlugin
name|qplug
init|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getQueryPlugin
argument_list|(
name|type
argument_list|)
decl_stmt|;
return|return
name|qplug
operator|.
name|createParser
argument_list|(
name|qstr
argument_list|,
name|localParams
argument_list|,
name|req
operator|.
name|getParams
argument_list|()
argument_list|,
name|req
argument_list|)
return|;
block|}
block|}
end_class
end_unit
