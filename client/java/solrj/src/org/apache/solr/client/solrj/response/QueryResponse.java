begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|response
package|;
end_package
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
name|HashMap
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
name|solr
operator|.
name|common
operator|.
name|SolrDocumentList
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
begin_comment
comment|/**  *   * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|QueryResponse
specifier|public
class|class
name|QueryResponse
extends|extends
name|SolrResponseBase
block|{
comment|// Direct pointers to known types
DECL|field|_header
specifier|private
name|NamedList
argument_list|<
name|Object
argument_list|>
name|_header
init|=
literal|null
decl_stmt|;
DECL|field|_results
specifier|private
name|SolrDocumentList
name|_results
init|=
literal|null
decl_stmt|;
DECL|field|_facetInfo
specifier|private
name|NamedList
argument_list|<
name|Object
argument_list|>
name|_facetInfo
init|=
literal|null
decl_stmt|;
DECL|field|_debugInfo
specifier|private
name|NamedList
argument_list|<
name|Object
argument_list|>
name|_debugInfo
init|=
literal|null
decl_stmt|;
DECL|field|_highlightingInfo
specifier|private
name|NamedList
argument_list|<
name|Object
argument_list|>
name|_highlightingInfo
init|=
literal|null
decl_stmt|;
comment|// Facet stuff
DECL|field|_facetQuery
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|_facetQuery
init|=
literal|null
decl_stmt|;
DECL|field|_facetFields
specifier|private
name|List
argument_list|<
name|FacetField
argument_list|>
name|_facetFields
init|=
literal|null
decl_stmt|;
DECL|field|_limitingFacets
specifier|private
name|List
argument_list|<
name|FacetField
argument_list|>
name|_limitingFacets
init|=
literal|null
decl_stmt|;
comment|// Highlight Info
DECL|field|_highlighting
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|>
name|_highlighting
init|=
literal|null
decl_stmt|;
comment|// Debug Info
DECL|field|_debugMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|_debugMap
init|=
literal|null
decl_stmt|;
DECL|field|_docIdMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|_docIdMap
init|=
literal|null
decl_stmt|;
DECL|field|_explainMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|_explainMap
init|=
literal|null
decl_stmt|;
DECL|method|QueryResponse
specifier|public
name|QueryResponse
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|res
parameter_list|)
block|{
name|super
argument_list|(
name|res
argument_list|)
expr_stmt|;
comment|// Look for known things
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|res
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|n
init|=
name|res
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"responseHeader"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|_header
operator|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|res
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"response"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|_results
operator|=
operator|(
name|SolrDocumentList
operator|)
name|res
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"facet_counts"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|_facetInfo
operator|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|res
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|extractFacetInfo
argument_list|(
name|_facetInfo
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"debug"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|_debugInfo
operator|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|res
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|extractDebugInfo
argument_list|(
name|_debugInfo
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"highlighting"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|_highlightingInfo
operator|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|res
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|extractHighlightingInfo
argument_list|(
name|_highlightingInfo
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|field|DKEY
specifier|private
specifier|static
specifier|final
name|String
name|DKEY
init|=
literal|",internal_docid="
decl_stmt|;
DECL|method|extractDebugInfo
specifier|private
name|void
name|extractDebugInfo
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|debug
parameter_list|)
block|{
name|_debugMap
operator|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
comment|// keep the order
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|info
range|:
name|debug
control|)
block|{
name|_debugMap
operator|.
name|put
argument_list|(
name|info
operator|.
name|getKey
argument_list|()
argument_list|,
name|info
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Parse out interisting bits from the debug info
name|_explainMap
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|_docIdMap
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
name|NamedList
argument_list|<
name|String
argument_list|>
name|explain
init|=
operator|(
name|NamedList
argument_list|<
name|String
argument_list|>
operator|)
name|_debugMap
operator|.
name|get
argument_list|(
literal|"explain"
argument_list|)
decl_stmt|;
if|if
condition|(
name|explain
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|info
range|:
name|explain
control|)
block|{
name|String
name|key
init|=
name|info
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|int
name|idx0
init|=
name|key
operator|.
name|indexOf
argument_list|(
literal|'='
argument_list|)
operator|+
literal|1
decl_stmt|;
name|int
name|idx1
init|=
name|info
operator|.
name|getKey
argument_list|()
operator|.
name|indexOf
argument_list|(
name|DKEY
argument_list|)
decl_stmt|;
name|int
name|idx2
init|=
name|idx1
operator|+
name|DKEY
operator|.
name|length
argument_list|()
decl_stmt|;
name|String
name|id
init|=
name|key
operator|.
name|substring
argument_list|(
name|idx0
argument_list|,
name|idx1
argument_list|)
decl_stmt|;
name|String
name|docID
init|=
name|key
operator|.
name|substring
argument_list|(
name|idx2
argument_list|)
decl_stmt|;
name|_explainMap
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|info
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|_docIdMap
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|docID
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|extractHighlightingInfo
specifier|private
name|void
name|extractHighlightingInfo
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|info
parameter_list|)
block|{
name|_highlighting
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|doc
range|:
name|info
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|fieldMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|_highlighting
operator|.
name|put
argument_list|(
name|doc
operator|.
name|getKey
argument_list|()
argument_list|,
name|fieldMap
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|fnl
init|=
operator|(
name|NamedList
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
operator|)
name|doc
operator|.
name|getValue
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|field
range|:
name|fnl
control|)
block|{
name|fieldMap
operator|.
name|put
argument_list|(
name|field
operator|.
name|getKey
argument_list|()
argument_list|,
name|field
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|extractFacetInfo
specifier|private
name|void
name|extractFacetInfo
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|info
parameter_list|)
block|{
comment|// Parse the queries
name|_facetQuery
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
name|NamedList
argument_list|<
name|Integer
argument_list|>
name|fq
init|=
operator|(
name|NamedList
argument_list|<
name|Integer
argument_list|>
operator|)
name|info
operator|.
name|get
argument_list|(
literal|"facet_queries"
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|entry
range|:
name|fq
control|)
block|{
name|_facetQuery
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Parse the facet info into fields
comment|// TODO?? The list could be<int> or<long>?  If always<long> then we can switch to<Long>
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Number
argument_list|>
argument_list|>
name|ff
init|=
operator|(
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Number
argument_list|>
argument_list|>
operator|)
name|info
operator|.
name|get
argument_list|(
literal|"facet_fields"
argument_list|)
decl_stmt|;
if|if
condition|(
name|ff
operator|!=
literal|null
condition|)
block|{
name|_facetFields
operator|=
operator|new
name|ArrayList
argument_list|<
name|FacetField
argument_list|>
argument_list|(
name|ff
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|_limitingFacets
operator|=
operator|new
name|ArrayList
argument_list|<
name|FacetField
argument_list|>
argument_list|(
name|ff
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|minsize
init|=
name|_results
operator|.
name|getNumFound
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|NamedList
argument_list|<
name|Number
argument_list|>
argument_list|>
name|facet
range|:
name|ff
control|)
block|{
name|FacetField
name|f
init|=
operator|new
name|FacetField
argument_list|(
name|facet
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Number
argument_list|>
name|entry
range|:
name|facet
operator|.
name|getValue
argument_list|()
control|)
block|{
name|f
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|_facetFields
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|FacetField
name|nl
init|=
name|f
operator|.
name|getLimitingFields
argument_list|(
name|minsize
argument_list|)
decl_stmt|;
if|if
condition|(
name|nl
operator|.
name|getValueCount
argument_list|()
operator|>
literal|0
condition|)
block|{
name|_limitingFacets
operator|.
name|add
argument_list|(
name|nl
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|//------------------------------------------------------
comment|//------------------------------------------------------
comment|/**    * Remove the field facet info    */
DECL|method|removeFacets
specifier|public
name|void
name|removeFacets
parameter_list|()
block|{
name|_facetFields
operator|=
operator|new
name|ArrayList
argument_list|<
name|FacetField
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|//------------------------------------------------------
comment|//------------------------------------------------------
DECL|method|getHeader
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|getHeader
parameter_list|()
block|{
return|return
name|_header
return|;
block|}
DECL|method|getResults
specifier|public
name|SolrDocumentList
name|getResults
parameter_list|()
block|{
return|return
name|_results
return|;
block|}
DECL|method|getDebugMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getDebugMap
parameter_list|()
block|{
return|return
name|_debugMap
return|;
block|}
DECL|method|getDocIdMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|getDocIdMap
parameter_list|()
block|{
return|return
name|_docIdMap
return|;
block|}
DECL|method|getExplainMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getExplainMap
parameter_list|()
block|{
return|return
name|_explainMap
return|;
block|}
DECL|method|getFacetQuery
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|getFacetQuery
parameter_list|()
block|{
return|return
name|_facetQuery
return|;
block|}
DECL|method|getHighlighting
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|>
name|getHighlighting
parameter_list|()
block|{
return|return
name|_highlighting
return|;
block|}
DECL|method|getFacetFields
specifier|public
name|List
argument_list|<
name|FacetField
argument_list|>
name|getFacetFields
parameter_list|()
block|{
return|return
name|_facetFields
return|;
block|}
comment|/** get     *     * @param name the name of the     * @return the FacetField by name or null if it does not exist    */
DECL|method|getFacetField
specifier|public
name|FacetField
name|getFacetField
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|_facetFields
operator|==
literal|null
condition|)
return|return
literal|null
return|;
for|for
control|(
name|FacetField
name|f
range|:
name|_facetFields
control|)
block|{
if|if
condition|(
name|f
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
return|return
name|f
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getLimitingFacets
specifier|public
name|List
argument_list|<
name|FacetField
argument_list|>
name|getLimitingFacets
parameter_list|()
block|{
return|return
name|_limitingFacets
return|;
block|}
block|}
end_class
end_unit
