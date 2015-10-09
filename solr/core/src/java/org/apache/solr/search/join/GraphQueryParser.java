begin_unit
begin_package
DECL|package|org.apache.solr.search.join
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|join
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|SyntaxError
import|;
end_import
begin_comment
comment|/**  * Solr query parser that will handle parsing graph query requests.  */
end_comment
begin_class
DECL|class|GraphQueryParser
specifier|public
class|class
name|GraphQueryParser
extends|extends
name|QParser
block|{
DECL|method|GraphQueryParser
specifier|public
name|GraphQueryParser
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
comment|// grab query params and defaults
name|SolrParams
name|localParams
init|=
name|getLocalParams
argument_list|()
decl_stmt|;
name|Query
name|rootNodeQuery
init|=
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
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|String
name|traversalFilterS
init|=
name|localParams
operator|.
name|get
argument_list|(
literal|"traversalFilter"
argument_list|)
decl_stmt|;
name|Query
name|traversalFilter
init|=
name|traversalFilterS
operator|==
literal|null
condition|?
literal|null
else|:
name|subQuery
argument_list|(
name|traversalFilterS
argument_list|,
literal|null
argument_list|)
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|String
name|fromField
init|=
name|localParams
operator|.
name|get
argument_list|(
literal|"from"
argument_list|,
literal|"node_id"
argument_list|)
decl_stmt|;
name|String
name|toField
init|=
name|localParams
operator|.
name|get
argument_list|(
literal|"to"
argument_list|,
literal|"edge_ids"
argument_list|)
decl_stmt|;
comment|// only documents that do not have values in the edge id fields.
name|boolean
name|onlyLeafNodes
init|=
name|localParams
operator|.
name|getBool
argument_list|(
literal|"returnOnlyLeaf"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// choose if you want to return documents that match the initial query or not.
name|boolean
name|returnRootNodes
init|=
name|localParams
operator|.
name|getBool
argument_list|(
literal|"returnRoot"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// enable or disable the use of an automaton term for the frontier traversal.
name|int
name|maxDepth
init|=
name|localParams
operator|.
name|getInt
argument_list|(
literal|"maxDepth"
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|// if true, an automaton will be compiled to issue the next graph hop
comment|// this avoid having a large number of boolean clauses. (and it's faster too!)
name|boolean
name|useAutn
init|=
name|localParams
operator|.
name|getBool
argument_list|(
literal|"useAutn"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// Construct a graph query object based on parameters passed in.
name|GraphQuery
name|gq
init|=
operator|new
name|GraphQuery
argument_list|(
name|rootNodeQuery
argument_list|,
name|fromField
argument_list|,
name|toField
argument_list|,
name|traversalFilter
argument_list|)
decl_stmt|;
comment|// set additional parameters that are not in the constructor.
name|gq
operator|.
name|setMaxDepth
argument_list|(
name|maxDepth
argument_list|)
expr_stmt|;
name|gq
operator|.
name|setOnlyLeafNodes
argument_list|(
name|onlyLeafNodes
argument_list|)
expr_stmt|;
name|gq
operator|.
name|setReturnRoot
argument_list|(
name|returnRootNodes
argument_list|)
expr_stmt|;
name|gq
operator|.
name|setUseAutn
argument_list|(
name|useAutn
argument_list|)
expr_stmt|;
comment|// return the parsed graph query.
return|return
name|gq
return|;
block|}
block|}
end_class
end_unit
