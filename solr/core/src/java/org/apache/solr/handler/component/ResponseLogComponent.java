begin_unit
begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Collections
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
name|response
operator|.
name|ResultContext
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
name|SolrIndexSearcher
import|;
end_import
begin_comment
comment|/**  * Adds to the log file the document IDs that are sent in the query response.  * If document scores are available in the response (by adding the pseudo-  * column 'score' to the field list) then each document ID will be followed  * by its score, as in:  *<pre>  * "... hits=55 responseLog=22:0.71231794,44:0.61231794 status=0 ..."  *</pre>  *   * Add it to a requestHandler in solrconfig.xml like this:  *<pre class="prettyprint">  *&lt;searchComponent name="responselog" class="solr.ResponseLogComponent"/&gt;  *   *&lt;requestHandler name="/select" class="solr.SearchHandler"&gt;  *&lt;lst name="defaults"&gt;  *     *     ...  *       *&lt;/lst&gt;  *&lt;arr name="components"&gt;  *&lt;str&gt;responselog&lt;/str&gt;  *&lt;/arr&gt;  *&lt;/requestHandler&gt;</pre>  *    *  It can then be enabled at query time by supplying<pre>responseLog=true</pre>  *  query parameter.  */
end_comment
begin_class
DECL|class|ResponseLogComponent
specifier|public
class|class
name|ResponseLogComponent
extends|extends
name|SearchComponent
block|{
DECL|field|COMPONENT_NAME
specifier|public
specifier|static
specifier|final
name|String
name|COMPONENT_NAME
init|=
literal|"responseLog"
decl_stmt|;
annotation|@
name|Override
DECL|method|prepare
specifier|public
name|void
name|prepare
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
DECL|method|process
specifier|public
name|void
name|process
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrParams
name|params
init|=
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|params
operator|.
name|getBool
argument_list|(
name|COMPONENT_NAME
argument_list|,
literal|false
argument_list|)
condition|)
return|return;
name|SolrIndexSearcher
name|searcher
init|=
name|rb
operator|.
name|req
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|searcher
operator|.
name|getSchema
argument_list|()
decl_stmt|;
if|if
condition|(
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
operator|==
literal|null
condition|)
return|return;
name|ResultContext
name|rc
init|=
operator|(
name|ResultContext
operator|)
name|rb
operator|.
name|rsp
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|"response"
argument_list|)
decl_stmt|;
if|if
condition|(
name|rc
operator|.
name|docs
operator|.
name|hasScores
argument_list|()
condition|)
block|{
name|processScores
argument_list|(
name|rb
argument_list|,
name|rc
operator|.
name|docs
argument_list|,
name|schema
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|processIds
argument_list|(
name|rb
argument_list|,
name|rc
operator|.
name|docs
argument_list|,
name|schema
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|processIds
specifier|protected
name|void
name|processIds
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|DocList
name|dl
parameter_list|,
name|IndexSchema
name|schema
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|fields
init|=
name|Collections
operator|.
name|singleton
argument_list|(
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|DocIterator
name|iter
init|=
name|dl
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|schema
operator|.
name|printableUniqueKey
argument_list|(
name|searcher
operator|.
name|doc
argument_list|(
name|iter
operator|.
name|nextDoc
argument_list|()
argument_list|,
name|fields
argument_list|)
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|rb
operator|.
name|rsp
operator|.
name|addToLog
argument_list|(
literal|"responseLog"
argument_list|,
name|sb
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|processScores
specifier|protected
name|void
name|processScores
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|DocList
name|dl
parameter_list|,
name|IndexSchema
name|schema
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|fields
init|=
name|Collections
operator|.
name|singleton
argument_list|(
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|DocIterator
name|iter
init|=
name|dl
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|schema
operator|.
name|printableUniqueKey
argument_list|(
name|searcher
operator|.
name|doc
argument_list|(
name|iter
operator|.
name|nextDoc
argument_list|()
argument_list|,
name|fields
argument_list|)
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
operator|.
name|append
argument_list|(
name|iter
operator|.
name|score
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|rb
operator|.
name|rsp
operator|.
name|addToLog
argument_list|(
literal|"responseLog"
argument_list|,
name|sb
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
literal|"A component that inserts the retrieved documents (and optionally scores) into the response log entry"
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
block|}
end_class
end_unit
