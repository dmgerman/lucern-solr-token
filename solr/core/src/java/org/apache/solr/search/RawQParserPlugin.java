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
begin_comment
comment|/**  * Create a term query from the input value without any text analysis or transformation whatsoever.  * This is useful in debugging, or when raw terms are returned from the terms component (this is not the default).  *  *<br>For easy filter construction to drill down in faceting, the {@link TermQParserPlugin} is recommended.  *<br>For full analysis on all fields, including text fields, see the {@link FieldQParserPlugin}.   *  *<br>Other parameters:<code>f</code>, the field  *<br>Example:<code>{!raw f=myfield}Foo Bar</code> creates<code>TermQuery(Term("myfield","Foo Bar"))</code>  */
end_comment
begin_class
DECL|class|RawQParserPlugin
specifier|public
class|class
name|RawQParserPlugin
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
literal|"raw"
decl_stmt|;
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
annotation|@
name|Override
specifier|public
name|Query
name|parse
parameter_list|()
block|{
return|return
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|localParams
operator|.
name|get
argument_list|(
name|QueryParsing
operator|.
name|F
argument_list|)
argument_list|,
name|localParams
operator|.
name|get
argument_list|(
name|QueryParsing
operator|.
name|V
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
