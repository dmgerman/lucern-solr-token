begin_unit
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
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
name|index
operator|.
name|TermEnum
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
name|queryParser
operator|.
name|QueryParser
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
name|*
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
name|core
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
name|request
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
name|request
operator|.
name|DefaultSolrParams
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
name|FieldType
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
name|*
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
name|util
operator|.
name|BoundedTreeSet
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
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Level
import|;
end_import
begin_comment
comment|/**  * A class that generates simple Facet information for a request.  *  * More advanced facet implementations may compose or subclass this class   * to leverage any of it's functionality.  */
end_comment
begin_class
DECL|class|SimpleFacets
specifier|public
class|class
name|SimpleFacets
block|{
comment|/** The main set of documents all facet counts should be relative to */
DECL|field|docs
specifier|protected
name|DocSet
name|docs
decl_stmt|;
comment|/** Configuration params behavior should be driven by */
DECL|field|params
specifier|protected
name|SolrParams
name|params
decl_stmt|;
comment|/** Searcher to use for all calculations */
DECL|field|searcher
specifier|protected
name|SolrIndexSearcher
name|searcher
decl_stmt|;
DECL|method|SimpleFacets
specifier|public
name|SimpleFacets
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|DocSet
name|docs
parameter_list|,
name|SolrParams
name|params
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
name|docs
operator|=
name|docs
expr_stmt|;
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
block|}
comment|/**    * Looks at various Params to determing if any simple Facet Constraint count    * computations are desired.    *    * @see #getFacetQueryCounts    * @see #getFacetFieldCounts    * @see SolrParams#FACET    * @return a NamedList of Facet Count info or null    */
DECL|method|getFacetCounts
specifier|public
name|NamedList
name|getFacetCounts
parameter_list|()
block|{
comment|// if someone called this method, benefit of the doubt: assume true
if|if
condition|(
operator|!
name|params
operator|.
name|getBool
argument_list|(
name|params
operator|.
name|FACET
argument_list|,
literal|true
argument_list|)
condition|)
return|return
literal|null
return|;
name|NamedList
name|res
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
try|try
block|{
name|res
operator|.
name|add
argument_list|(
literal|"facet_queries"
argument_list|,
name|getFacetQueryCounts
argument_list|()
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"facet_fields"
argument_list|,
name|getFacetFieldCounts
argument_list|()
argument_list|)
expr_stmt|;
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
literal|"Exception during facet counts"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"exception"
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
return|return
name|res
return|;
block|}
comment|/**    * Returns a list of facet counts for each of the facet queries     * specified in the params    *    * @see SolrParams#FACET_QUERY    */
DECL|method|getFacetQueryCounts
specifier|public
name|NamedList
name|getFacetQueryCounts
parameter_list|()
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|NamedList
name|res
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
comment|/* Ignore SolrParams.DF - could have init param facet.query assuming      * the schema default with query param DF intented to only affect Q.      * If user doesn't want schema default for facet.query, they should be      * explicit.      */
name|SolrQueryParser
name|qp
init|=
operator|new
name|SolrQueryParser
argument_list|(
name|searcher
operator|.
name|getSchema
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
index|[]
name|facetQs
init|=
name|params
operator|.
name|getParams
argument_list|(
name|SolrParams
operator|.
name|FACET_QUERY
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|facetQs
operator|&&
literal|0
operator|!=
name|facetQs
operator|.
name|length
condition|)
block|{
for|for
control|(
name|String
name|q
range|:
name|facetQs
control|)
block|{
name|res
operator|.
name|add
argument_list|(
name|q
argument_list|,
name|searcher
operator|.
name|numDocs
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
name|q
argument_list|)
argument_list|,
name|docs
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|res
return|;
block|}
comment|/**    * Returns a list of value constraints and the associated facet counts     * for each facet field specified in the params.    *    * @see SolrParams#FACET_FIELD    * @see #getFacetFieldMissingCount    * @see #getFacetTermEnumCounts    */
DECL|method|getFacetFieldCounts
specifier|public
name|NamedList
name|getFacetFieldCounts
parameter_list|()
throws|throws
name|IOException
block|{
name|NamedList
name|res
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|String
index|[]
name|facetFs
init|=
name|params
operator|.
name|getParams
argument_list|(
name|SolrParams
operator|.
name|FACET_FIELD
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|facetFs
operator|&&
literal|0
operator|!=
name|facetFs
operator|.
name|length
condition|)
block|{
for|for
control|(
name|String
name|f
range|:
name|facetFs
control|)
block|{
name|NamedList
name|counts
init|=
name|getFacetTermEnumCounts
argument_list|(
name|f
argument_list|)
decl_stmt|;
if|if
condition|(
name|params
operator|.
name|getFieldBool
argument_list|(
name|f
argument_list|,
name|params
operator|.
name|FACET_MISSING
argument_list|,
literal|false
argument_list|)
condition|)
name|counts
operator|.
name|add
argument_list|(
literal|null
argument_list|,
name|getFacetFieldMissingCount
argument_list|(
name|f
argument_list|)
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
name|f
argument_list|,
name|counts
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|res
return|;
block|}
comment|/**    * Returns a count of the documents in the set which do not have any     * terms for for the specified field.    *    * @see SolrParams#FACET_MISSING    */
DECL|method|getFacetFieldMissingCount
specifier|public
name|int
name|getFacetFieldMissingCount
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
name|DocSet
name|hasVal
init|=
name|searcher
operator|.
name|getDocSet
argument_list|(
operator|new
name|ConstantScoreRangeQuery
argument_list|(
name|fieldName
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|docs
operator|.
name|andNotSize
argument_list|(
name|hasVal
argument_list|)
return|;
block|}
comment|/**    * Returns a list of terms in the specified field along with the     * corrisponding count of documents in the set that match that constraint.    *    * @see SolrParams#FACET_LIMIT    * @see SolrParams#FACET_ZEROS    */
DECL|method|getFacetTermEnumCounts
specifier|public
name|NamedList
name|getFacetTermEnumCounts
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
comment|/* :TODO: potential optimization...      * cache the Terms with the highest docFreq and try them first      * don't enum if we get our max from them      */
name|IndexSchema
name|schema
init|=
name|searcher
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|IndexReader
name|r
init|=
name|searcher
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|FieldType
name|ft
init|=
name|schema
operator|.
name|getFieldType
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|CountPair
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|counts
init|=
operator|new
name|HashSet
argument_list|<
name|CountPair
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|limit
init|=
name|params
operator|.
name|getFieldInt
argument_list|(
name|fieldName
argument_list|,
name|params
operator|.
name|FACET_LIMIT
argument_list|,
literal|100
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0
operator|<=
name|limit
condition|)
block|{
name|counts
operator|=
operator|new
name|BoundedTreeSet
argument_list|<
name|CountPair
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
argument_list|(
name|limit
argument_list|)
expr_stmt|;
block|}
name|boolean
name|zeros
init|=
name|params
operator|.
name|getFieldBool
argument_list|(
name|fieldName
argument_list|,
name|params
operator|.
name|FACET_ZEROS
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|TermEnum
name|te
init|=
name|r
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
literal|""
argument_list|)
argument_list|)
decl_stmt|;
do|do
block|{
name|Term
name|t
init|=
name|te
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|t
operator|||
operator|!
name|t
operator|.
name|field
argument_list|()
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
break|break;
if|if
condition|(
literal|0
operator|<
name|te
operator|.
name|docFreq
argument_list|()
condition|)
block|{
comment|/* all docs may be deleted */
name|int
name|count
init|=
name|searcher
operator|.
name|numDocs
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|t
argument_list|)
argument_list|,
name|docs
argument_list|)
decl_stmt|;
comment|/* :TODO: is indexedToReadable correct? */
if|if
condition|(
name|zeros
operator|||
literal|0
operator|<
name|count
condition|)
name|counts
operator|.
name|add
argument_list|(
operator|new
name|CountPair
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|(
name|ft
operator|.
name|indexedToReadable
argument_list|(
name|t
operator|.
name|text
argument_list|()
argument_list|)
argument_list|,
name|count
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
name|te
operator|.
name|next
argument_list|()
condition|)
do|;
name|NamedList
name|res
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
for|for
control|(
name|CountPair
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|p
range|:
name|counts
control|)
block|{
name|res
operator|.
name|add
argument_list|(
name|p
operator|.
name|key
argument_list|,
name|p
operator|.
name|val
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
comment|/**    * A simple key=>val pair whose natural order is such that     *<b>higher</b> vals come before lower vals.    * In case of tie vals, then<b>lower</b> keys come before higher keys.    */
DECL|class|CountPair
specifier|public
specifier|static
class|class
name|CountPair
parameter_list|<
name|K
extends|extends
name|Comparable
parameter_list|<
name|?
super|super
name|K
parameter_list|>
parameter_list|,
name|V
extends|extends
name|Comparable
parameter_list|<
name|?
super|super
name|V
parameter_list|>
parameter_list|>
implements|implements
name|Comparable
argument_list|<
name|CountPair
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
block|{
DECL|method|CountPair
specifier|public
name|CountPair
parameter_list|(
name|K
name|k
parameter_list|,
name|V
name|v
parameter_list|)
block|{
name|key
operator|=
name|k
expr_stmt|;
name|val
operator|=
name|v
expr_stmt|;
block|}
DECL|field|key
specifier|public
name|K
name|key
decl_stmt|;
DECL|field|val
specifier|public
name|V
name|val
decl_stmt|;
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|key
operator|.
name|hashCode
argument_list|()
operator|^
name|val
operator|.
name|hashCode
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
return|return
operator|(
name|o
operator|instanceof
name|CountPair
operator|)
operator|&&
operator|(
literal|0
operator|==
name|this
operator|.
name|compareTo
argument_list|(
operator|(
name|CountPair
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
operator|)
name|o
argument_list|)
operator|)
return|;
block|}
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|CountPair
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|o
parameter_list|)
block|{
name|int
name|vc
init|=
name|o
operator|.
name|val
operator|.
name|compareTo
argument_list|(
name|val
argument_list|)
decl_stmt|;
return|return
operator|(
literal|0
operator|!=
name|vc
condition|?
name|vc
else|:
name|key
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|key
argument_list|)
operator|)
return|;
block|}
block|}
block|}
end_class
end_unit
