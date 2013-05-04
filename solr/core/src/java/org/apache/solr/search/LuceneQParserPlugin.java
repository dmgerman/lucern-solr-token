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
name|Sort
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
name|common
operator|.
name|util
operator|.
name|StrUtils
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_comment
comment|/**  * Parse Solr's variant on the Lucene QueryParser syntax.  *<br>Other parameters:<ul>  *<li>q.op - the default operator "OR" or "AND"</li>  *<li>df - the default field name</li>  *</ul>  *<br>Example:<code>{!lucene q.op=AND df=text sort='price asc'}myfield:foo +bar -baz</code>  */
end_comment
begin_class
DECL|class|LuceneQParserPlugin
specifier|public
class|class
name|LuceneQParserPlugin
extends|extends
name|QParserPlugin
block|{
DECL|field|NAME
specifier|public
specifier|static
name|String
name|NAME
init|=
literal|"lucene"
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
block|{   }
annotation|@
name|Override
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
name|LuceneQParser
argument_list|(
name|qstr
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
return|;
block|}
block|}
end_class
begin_class
DECL|class|OldLuceneQParser
class|class
name|OldLuceneQParser
extends|extends
name|LuceneQParser
block|{
DECL|field|sortStr
name|String
name|sortStr
decl_stmt|;
DECL|method|OldLuceneQParser
specifier|public
name|OldLuceneQParser
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
name|super
argument_list|(
name|qstr
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|()
throws|throws
name|SyntaxError
block|{
comment|// handle legacy "query;sort" syntax
if|if
condition|(
name|getLocalParams
argument_list|()
operator|==
literal|null
condition|)
block|{
name|String
name|qstr
init|=
name|getString
argument_list|()
decl_stmt|;
if|if
condition|(
name|qstr
operator|==
literal|null
operator|||
name|qstr
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|null
return|;
name|sortStr
operator|=
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|SORT
argument_list|)
expr_stmt|;
if|if
condition|(
name|sortStr
operator|==
literal|null
condition|)
block|{
comment|// sort may be legacy form, included in the query string
name|List
argument_list|<
name|String
argument_list|>
name|commands
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|qstr
argument_list|,
literal|';'
argument_list|)
decl_stmt|;
if|if
condition|(
name|commands
operator|.
name|size
argument_list|()
operator|==
literal|2
condition|)
block|{
name|qstr
operator|=
name|commands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|sortStr
operator|=
name|commands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|commands
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// This is need to support the case where someone sends: "q=query;"
name|qstr
operator|=
name|commands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|commands
operator|.
name|size
argument_list|()
operator|>
literal|2
condition|)
block|{
throw|throw
operator|new
name|SyntaxError
argument_list|(
literal|"If you want to use multiple ';' in the query, use the 'sort' param."
argument_list|)
throw|;
block|}
block|}
name|setString
argument_list|(
name|qstr
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|parse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getSort
specifier|public
name|SortSpec
name|getSort
parameter_list|(
name|boolean
name|useGlobal
parameter_list|)
throws|throws
name|SyntaxError
block|{
name|SortSpec
name|sort
init|=
name|super
operator|.
name|getSort
argument_list|(
name|useGlobal
argument_list|)
decl_stmt|;
if|if
condition|(
name|sortStr
operator|!=
literal|null
operator|&&
name|sortStr
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
name|sort
operator|.
name|getSort
argument_list|()
operator|==
literal|null
condition|)
block|{
name|Sort
name|oldSort
init|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
name|sortStr
argument_list|,
name|getReq
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldSort
operator|!=
literal|null
condition|)
block|{
name|sort
operator|.
name|sort
operator|=
name|oldSort
expr_stmt|;
block|}
block|}
return|return
name|sort
return|;
block|}
block|}
end_class
end_unit
