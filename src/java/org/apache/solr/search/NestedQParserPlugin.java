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
name|search
operator|.
name|function
operator|.
name|BoostedQuery
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
name|function
operator|.
name|FunctionQuery
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
name|function
operator|.
name|QueryValueSource
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
name|function
operator|.
name|ValueSource
import|;
end_import
begin_comment
comment|/**  * Create a nested query, with the ability of that query to redefine it's type via  * local parameters.  This is useful in specifying defaults in configuration and  * letting clients indirectly reference them.  *<br>Example:<code>&lt;!query defType=func v=$q1&gt;</code>  *<br> if the q1 parameter is<code>price</code> then the query would be a function query on the price field.  *<br> if the q1 parameter is<code>&lt;!lucene&gt;inStock:true</code> then a term query is  *     created from the lucene syntax string that matches documents with inStock=true.  */
end_comment
begin_class
DECL|class|NestedQParserPlugin
specifier|public
class|class
name|NestedQParserPlugin
extends|extends
name|QParserPlugin
block|{
DECL|field|NAME
specifier|public
specifier|static
name|String
name|NAME
init|=
literal|"query"
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
return|return
operator|new
name|QParser
argument_list|(
name|qstr
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
block|{
name|QParser
name|baseParser
decl_stmt|;
name|ValueSource
name|vs
decl_stmt|;
name|String
name|b
decl_stmt|;
specifier|public
name|Query
name|parse
parameter_list|()
throws|throws
name|ParseException
block|{
name|baseParser
operator|=
name|subQuery
argument_list|(
name|localParams
operator|.
name|get
argument_list|(
name|QueryParsing
operator|.
name|V
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
name|baseParser
operator|.
name|getQuery
argument_list|()
return|;
block|}
specifier|public
name|String
index|[]
name|getDefaultHighlightFields
parameter_list|()
block|{
return|return
name|baseParser
operator|.
name|getDefaultHighlightFields
argument_list|()
return|;
block|}
specifier|public
name|Query
name|getHighlightQuery
parameter_list|()
throws|throws
name|ParseException
block|{
return|return
name|baseParser
operator|.
name|getHighlightQuery
argument_list|()
return|;
block|}
specifier|public
name|void
name|addDebugInfo
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|debugInfo
parameter_list|)
block|{
comment|// encapsulate base debug info in a sub-list?
name|baseParser
operator|.
name|addDebugInfo
argument_list|(
name|debugInfo
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
