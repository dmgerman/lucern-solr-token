begin_unit
begin_package
DECL|package|org.apache.lucene.queryparser.classic
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|classic
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
name|analysis
operator|.
name|Analyzer
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
name|MultiPhraseQuery
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
name|PhraseQuery
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
begin_comment
comment|/**  * A QueryParser which constructs queries to search multiple fields.  *  */
end_comment
begin_class
DECL|class|MultiFieldQueryParser
specifier|public
class|class
name|MultiFieldQueryParser
extends|extends
name|QueryParser
block|{
DECL|field|fields
specifier|protected
name|String
index|[]
name|fields
decl_stmt|;
DECL|field|boosts
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|boosts
decl_stmt|;
comment|/**    * Creates a MultiFieldQueryParser.     * Allows passing of a map with term to Boost, and the boost to apply to each term.    *    *<p>It will, when parse(String query)    * is called, construct a query like this (assuming the query consists of    * two terms and you specify the two fields<code>title</code> and<code>body</code>):</p>    *     *<code>    * (title:term1 body:term1) (title:term2 body:term2)    *</code>    *    *<p>When setDefaultOperator(AND_OPERATOR) is set, the result will be:</p>    *      *<code>    * +(title:term1 body:term1) +(title:term2 body:term2)    *</code>    *     *<p>When you pass a boost (title=>5 body=>10) you can get</p>    *     *<code>    * +(title:term1^5.0 body:term1^10.0) +(title:term2^5.0 body:term2^10.0)    *</code>    *    *<p>In other words, all the query's terms must appear, but it doesn't matter in    * what fields they appear.</p>    */
DECL|method|MultiFieldQueryParser
specifier|public
name|MultiFieldQueryParser
parameter_list|(
name|String
index|[]
name|fields
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|boosts
parameter_list|)
block|{
name|this
argument_list|(
name|fields
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|this
operator|.
name|boosts
operator|=
name|boosts
expr_stmt|;
block|}
comment|/**    * Creates a MultiFieldQueryParser.    *    *<p>It will, when parse(String query)    * is called, construct a query like this (assuming the query consists of    * two terms and you specify the two fields<code>title</code> and<code>body</code>):</p>    *     *<code>    * (title:term1 body:term1) (title:term2 body:term2)    *</code>    *    *<p>When setDefaultOperator(AND_OPERATOR) is set, the result will be:</p>    *      *<code>    * +(title:term1 body:term1) +(title:term2 body:term2)    *</code>    *     *<p>In other words, all the query's terms must appear, but it doesn't matter in    * what fields they appear.</p>    */
DECL|method|MultiFieldQueryParser
specifier|public
name|MultiFieldQueryParser
parameter_list|(
name|String
index|[]
name|fields
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFieldQuery
specifier|protected
name|Query
name|getFieldQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|queryText
parameter_list|,
name|int
name|slop
parameter_list|)
throws|throws
name|ParseException
block|{
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
name|List
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Query
name|q
init|=
name|super
operator|.
name|getFieldQuery
argument_list|(
name|fields
index|[
name|i
index|]
argument_list|,
name|queryText
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|q
operator|!=
literal|null
condition|)
block|{
comment|//If the user passes a map of boosts
if|if
condition|(
name|boosts
operator|!=
literal|null
condition|)
block|{
comment|//Get the boost from the map and apply them
name|Float
name|boost
init|=
name|boosts
operator|.
name|get
argument_list|(
name|fields
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|boost
operator|!=
literal|null
condition|)
block|{
name|q
operator|.
name|setBoost
argument_list|(
name|boost
operator|.
name|floatValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|applySlop
argument_list|(
name|q
argument_list|,
name|slop
argument_list|)
expr_stmt|;
name|clauses
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|q
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|clauses
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
comment|// happens for stopwords
return|return
literal|null
return|;
return|return
name|getBooleanQuery
argument_list|(
name|clauses
argument_list|,
literal|true
argument_list|)
return|;
block|}
name|Query
name|q
init|=
name|super
operator|.
name|getFieldQuery
argument_list|(
name|field
argument_list|,
name|queryText
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|applySlop
argument_list|(
name|q
argument_list|,
name|slop
argument_list|)
expr_stmt|;
return|return
name|q
return|;
block|}
DECL|method|applySlop
specifier|private
name|void
name|applySlop
parameter_list|(
name|Query
name|q
parameter_list|,
name|int
name|slop
parameter_list|)
block|{
if|if
condition|(
name|q
operator|instanceof
name|PhraseQuery
condition|)
block|{
operator|(
operator|(
name|PhraseQuery
operator|)
name|q
operator|)
operator|.
name|setSlop
argument_list|(
name|slop
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|q
operator|instanceof
name|MultiPhraseQuery
condition|)
block|{
operator|(
operator|(
name|MultiPhraseQuery
operator|)
name|q
operator|)
operator|.
name|setSlop
argument_list|(
name|slop
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getFieldQuery
specifier|protected
name|Query
name|getFieldQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|queryText
parameter_list|,
name|boolean
name|quoted
parameter_list|)
throws|throws
name|ParseException
block|{
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
name|List
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Query
name|q
init|=
name|super
operator|.
name|getFieldQuery
argument_list|(
name|fields
index|[
name|i
index|]
argument_list|,
name|queryText
argument_list|,
name|quoted
argument_list|)
decl_stmt|;
if|if
condition|(
name|q
operator|!=
literal|null
condition|)
block|{
comment|//If the user passes a map of boosts
if|if
condition|(
name|boosts
operator|!=
literal|null
condition|)
block|{
comment|//Get the boost from the map and apply them
name|Float
name|boost
init|=
name|boosts
operator|.
name|get
argument_list|(
name|fields
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|boost
operator|!=
literal|null
condition|)
block|{
name|q
operator|.
name|setBoost
argument_list|(
name|boost
operator|.
name|floatValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|clauses
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|q
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|clauses
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
comment|// happens for stopwords
return|return
literal|null
return|;
return|return
name|getBooleanQuery
argument_list|(
name|clauses
argument_list|,
literal|true
argument_list|)
return|;
block|}
name|Query
name|q
init|=
name|super
operator|.
name|getFieldQuery
argument_list|(
name|field
argument_list|,
name|queryText
argument_list|,
name|quoted
argument_list|)
decl_stmt|;
return|return
name|q
return|;
block|}
annotation|@
name|Override
DECL|method|getFuzzyQuery
specifier|protected
name|Query
name|getFuzzyQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|termStr
parameter_list|,
name|float
name|minSimilarity
parameter_list|)
throws|throws
name|ParseException
block|{
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
name|List
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|clauses
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|getFuzzyQuery
argument_list|(
name|fields
index|[
name|i
index|]
argument_list|,
name|termStr
argument_list|,
name|minSimilarity
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|getBooleanQuery
argument_list|(
name|clauses
argument_list|,
literal|true
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|getFuzzyQuery
argument_list|(
name|field
argument_list|,
name|termStr
argument_list|,
name|minSimilarity
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getPrefixQuery
specifier|protected
name|Query
name|getPrefixQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|termStr
parameter_list|)
throws|throws
name|ParseException
block|{
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
name|List
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|clauses
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|getPrefixQuery
argument_list|(
name|fields
index|[
name|i
index|]
argument_list|,
name|termStr
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|getBooleanQuery
argument_list|(
name|clauses
argument_list|,
literal|true
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|getPrefixQuery
argument_list|(
name|field
argument_list|,
name|termStr
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getWildcardQuery
specifier|protected
name|Query
name|getWildcardQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|termStr
parameter_list|)
throws|throws
name|ParseException
block|{
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
name|List
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|clauses
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|getWildcardQuery
argument_list|(
name|fields
index|[
name|i
index|]
argument_list|,
name|termStr
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|getBooleanQuery
argument_list|(
name|clauses
argument_list|,
literal|true
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|getWildcardQuery
argument_list|(
name|field
argument_list|,
name|termStr
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getRangeQuery
specifier|protected
name|Query
name|getRangeQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|part1
parameter_list|,
name|String
name|part2
parameter_list|,
name|boolean
name|startInclusive
parameter_list|,
name|boolean
name|endInclusive
parameter_list|)
throws|throws
name|ParseException
block|{
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
name|List
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|clauses
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|getRangeQuery
argument_list|(
name|fields
index|[
name|i
index|]
argument_list|,
name|part1
argument_list|,
name|part2
argument_list|,
name|startInclusive
argument_list|,
name|endInclusive
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|getBooleanQuery
argument_list|(
name|clauses
argument_list|,
literal|true
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|getRangeQuery
argument_list|(
name|field
argument_list|,
name|part1
argument_list|,
name|part2
argument_list|,
name|startInclusive
argument_list|,
name|endInclusive
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getRegexpQuery
specifier|protected
name|Query
name|getRegexpQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|termStr
parameter_list|)
throws|throws
name|ParseException
block|{
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
name|List
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|clauses
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|getRegexpQuery
argument_list|(
name|fields
index|[
name|i
index|]
argument_list|,
name|termStr
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|getBooleanQuery
argument_list|(
name|clauses
argument_list|,
literal|true
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|getRegexpQuery
argument_list|(
name|field
argument_list|,
name|termStr
argument_list|)
return|;
block|}
comment|/**    * Parses a query which searches on the fields specified.    *<p>    * If x fields are specified, this effectively constructs:    *<pre>    *<code>    * (field1:query1) (field2:query2) (field3:query3)...(fieldx:queryx)    *</code>    *</pre>    * @param queries Queries strings to parse    * @param fields Fields to search on    * @param analyzer Analyzer to use    * @throws ParseException if query parsing fails    * @throws IllegalArgumentException if the length of the queries array differs    *  from the length of the fields array    */
DECL|method|parse
specifier|public
specifier|static
name|Query
name|parse
parameter_list|(
name|String
index|[]
name|queries
parameter_list|,
name|String
index|[]
name|fields
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|ParseException
block|{
if|if
condition|(
name|queries
operator|.
name|length
operator|!=
name|fields
operator|.
name|length
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"queries.length != fields.length"
argument_list|)
throw|;
name|BooleanQuery
name|bQuery
init|=
operator|new
name|BooleanQuery
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
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|QueryParser
name|qp
init|=
operator|new
name|QueryParser
argument_list|(
name|fields
index|[
name|i
index|]
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|Query
name|q
init|=
name|qp
operator|.
name|parse
argument_list|(
name|queries
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|q
operator|!=
literal|null
operator|&&
comment|// q never null, just being defensive
operator|(
operator|!
operator|(
name|q
operator|instanceof
name|BooleanQuery
operator|)
operator|||
operator|(
operator|(
name|BooleanQuery
operator|)
name|q
operator|)
operator|.
name|getClauses
argument_list|()
operator|.
name|length
operator|>
literal|0
operator|)
condition|)
block|{
name|bQuery
operator|.
name|add
argument_list|(
name|q
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|bQuery
return|;
block|}
comment|/**    * Parses a query, searching on the fields specified.    * Use this if you need to specify certain fields as required,    * and others as prohibited.    *<p>    * Usage:    *<pre class="prettyprint">    *<code>    * String[] fields = {"filename", "contents", "description"};    * BooleanClause.Occur[] flags = {BooleanClause.Occur.SHOULD,    *                BooleanClause.Occur.MUST,    *                BooleanClause.Occur.MUST_NOT};    * MultiFieldQueryParser.parse("query", fields, flags, analyzer);    *</code>    *</pre>    *<p>    * The code above would construct a query:    *<pre>    *<code>    * (filename:query) +(contents:query) -(description:query)    *</code>    *</pre>    *    * @param query Query string to parse    * @param fields Fields to search on    * @param flags Flags describing the fields    * @param analyzer Analyzer to use    * @throws ParseException if query parsing fails    * @throws IllegalArgumentException if the length of the fields array differs    *  from the length of the flags array    */
DECL|method|parse
specifier|public
specifier|static
name|Query
name|parse
parameter_list|(
name|String
name|query
parameter_list|,
name|String
index|[]
name|fields
parameter_list|,
name|BooleanClause
operator|.
name|Occur
index|[]
name|flags
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|ParseException
block|{
if|if
condition|(
name|fields
operator|.
name|length
operator|!=
name|flags
operator|.
name|length
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"fields.length != flags.length"
argument_list|)
throw|;
name|BooleanQuery
name|bQuery
init|=
operator|new
name|BooleanQuery
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
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|QueryParser
name|qp
init|=
operator|new
name|QueryParser
argument_list|(
name|fields
index|[
name|i
index|]
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|Query
name|q
init|=
name|qp
operator|.
name|parse
argument_list|(
name|query
argument_list|)
decl_stmt|;
if|if
condition|(
name|q
operator|!=
literal|null
operator|&&
comment|// q never null, just being defensive
operator|(
operator|!
operator|(
name|q
operator|instanceof
name|BooleanQuery
operator|)
operator|||
operator|(
operator|(
name|BooleanQuery
operator|)
name|q
operator|)
operator|.
name|getClauses
argument_list|()
operator|.
name|length
operator|>
literal|0
operator|)
condition|)
block|{
name|bQuery
operator|.
name|add
argument_list|(
name|q
argument_list|,
name|flags
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|bQuery
return|;
block|}
comment|/**    * Parses a query, searching on the fields specified.    * Use this if you need to specify certain fields as required,    * and others as prohibited.    *<p>    * Usage:    *<pre class="prettyprint">    *<code>    * String[] query = {"query1", "query2", "query3"};    * String[] fields = {"filename", "contents", "description"};    * BooleanClause.Occur[] flags = {BooleanClause.Occur.SHOULD,    *                BooleanClause.Occur.MUST,    *                BooleanClause.Occur.MUST_NOT};    * MultiFieldQueryParser.parse(query, fields, flags, analyzer);    *</code>    *</pre>    *<p>    * The code above would construct a query:    *<pre>    *<code>    * (filename:query1) +(contents:query2) -(description:query3)    *</code>    *</pre>    *    * @param queries Queries string to parse    * @param fields Fields to search on    * @param flags Flags describing the fields    * @param analyzer Analyzer to use    * @throws ParseException if query parsing fails    * @throws IllegalArgumentException if the length of the queries, fields,    *  and flags array differ    */
DECL|method|parse
specifier|public
specifier|static
name|Query
name|parse
parameter_list|(
name|String
index|[]
name|queries
parameter_list|,
name|String
index|[]
name|fields
parameter_list|,
name|BooleanClause
operator|.
name|Occur
index|[]
name|flags
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|ParseException
block|{
if|if
condition|(
operator|!
operator|(
name|queries
operator|.
name|length
operator|==
name|fields
operator|.
name|length
operator|&&
name|queries
operator|.
name|length
operator|==
name|flags
operator|.
name|length
operator|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"queries, fields, and flags array have have different length"
argument_list|)
throw|;
name|BooleanQuery
name|bQuery
init|=
operator|new
name|BooleanQuery
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
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|QueryParser
name|qp
init|=
operator|new
name|QueryParser
argument_list|(
name|fields
index|[
name|i
index|]
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|Query
name|q
init|=
name|qp
operator|.
name|parse
argument_list|(
name|queries
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|q
operator|!=
literal|null
operator|&&
comment|// q never null, just being defensive
operator|(
operator|!
operator|(
name|q
operator|instanceof
name|BooleanQuery
operator|)
operator|||
operator|(
operator|(
name|BooleanQuery
operator|)
name|q
operator|)
operator|.
name|getClauses
argument_list|()
operator|.
name|length
operator|>
literal|0
operator|)
condition|)
block|{
name|bQuery
operator|.
name|add
argument_list|(
name|q
argument_list|,
name|flags
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|bQuery
return|;
block|}
block|}
end_class
end_unit
