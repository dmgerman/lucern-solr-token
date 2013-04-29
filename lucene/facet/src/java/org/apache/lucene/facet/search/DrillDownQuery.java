begin_unit
begin_package
DECL|package|org.apache.lucene.facet.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
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
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|facet
operator|.
name|params
operator|.
name|CategoryListParams
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
name|facet
operator|.
name|params
operator|.
name|FacetIndexingParams
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
name|facet
operator|.
name|taxonomy
operator|.
name|CategoryPath
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
name|BooleanClause
operator|.
name|Occur
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
name|ConstantScoreQuery
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
name|Filter
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
name|FilteredQuery
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
begin_comment
comment|/**  * A {@link Query} for drill-down over {@link CategoryPath categories}. You  * should call {@link #add(CategoryPath...)} for every group of categories you  * want to drill-down over. Each category in the group is {@code OR'ed} with  * the others, and groups are {@code AND'ed}.  *<p>  *<b>NOTE:</b> if you choose to create your own {@link Query} by calling  * {@link #term}, it is recommended to wrap it with {@link ConstantScoreQuery}  * and set the {@link ConstantScoreQuery#setBoost(float) boost} to {@code 0.0f},  * so that it does not affect the scores of the documents.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|DrillDownQuery
specifier|public
specifier|final
class|class
name|DrillDownQuery
extends|extends
name|Query
block|{
comment|/** Return a drill-down {@link Term} for a category. */
DECL|method|term
specifier|public
specifier|static
name|Term
name|term
parameter_list|(
name|FacetIndexingParams
name|iParams
parameter_list|,
name|CategoryPath
name|path
parameter_list|)
block|{
name|CategoryListParams
name|clp
init|=
name|iParams
operator|.
name|getCategoryListParams
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
name|path
operator|.
name|fullPathLength
argument_list|()
index|]
decl_stmt|;
name|iParams
operator|.
name|drillDownTermText
argument_list|(
name|path
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
return|return
operator|new
name|Term
argument_list|(
name|clp
operator|.
name|field
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|buffer
argument_list|)
argument_list|)
return|;
block|}
DECL|field|query
specifier|private
specifier|final
name|BooleanQuery
name|query
decl_stmt|;
DECL|field|drillDownDims
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|drillDownDims
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|fip
specifier|final
name|FacetIndexingParams
name|fip
decl_stmt|;
comment|/** Used by clone() */
DECL|method|DrillDownQuery
name|DrillDownQuery
parameter_list|(
name|FacetIndexingParams
name|fip
parameter_list|,
name|BooleanQuery
name|query
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|drillDownDims
parameter_list|)
block|{
name|this
operator|.
name|fip
operator|=
name|fip
expr_stmt|;
name|this
operator|.
name|query
operator|=
name|query
operator|.
name|clone
argument_list|()
expr_stmt|;
name|this
operator|.
name|drillDownDims
operator|.
name|putAll
argument_list|(
name|drillDownDims
argument_list|)
expr_stmt|;
block|}
comment|/** Used by DrillSideways */
DECL|method|DrillDownQuery
name|DrillDownQuery
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|DrillDownQuery
name|other
parameter_list|)
block|{
name|query
operator|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// disable coord
name|BooleanClause
index|[]
name|clauses
init|=
name|other
operator|.
name|query
operator|.
name|getClauses
argument_list|()
decl_stmt|;
if|if
condition|(
name|clauses
operator|.
name|length
operator|==
name|other
operator|.
name|drillDownDims
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot apply filter unless baseQuery isn't null; pass ConstantScoreQuery instead"
argument_list|)
throw|;
block|}
assert|assert
name|clauses
operator|.
name|length
operator|==
literal|1
operator|+
name|other
operator|.
name|drillDownDims
operator|.
name|size
argument_list|()
operator|:
name|clauses
operator|.
name|length
operator|+
literal|" vs "
operator|+
operator|(
literal|1
operator|+
name|other
operator|.
name|drillDownDims
operator|.
name|size
argument_list|()
operator|)
assert|;
name|drillDownDims
operator|.
name|putAll
argument_list|(
name|other
operator|.
name|drillDownDims
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|FilteredQuery
argument_list|(
name|clauses
index|[
literal|0
index|]
operator|.
name|getQuery
argument_list|()
argument_list|,
name|filter
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|clauses
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|query
operator|.
name|add
argument_list|(
name|clauses
index|[
name|i
index|]
operator|.
name|getQuery
argument_list|()
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
name|fip
operator|=
name|other
operator|.
name|fip
expr_stmt|;
block|}
comment|/** Used by DrillSideways */
DECL|method|DrillDownQuery
name|DrillDownQuery
parameter_list|(
name|FacetIndexingParams
name|fip
parameter_list|,
name|Query
name|baseQuery
parameter_list|,
name|List
argument_list|<
name|Query
argument_list|>
name|clauses
parameter_list|)
block|{
name|this
operator|.
name|fip
operator|=
name|fip
expr_stmt|;
name|this
operator|.
name|query
operator|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|baseQuery
operator|!=
literal|null
condition|)
block|{
name|query
operator|.
name|add
argument_list|(
name|baseQuery
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Query
name|clause
range|:
name|clauses
control|)
block|{
name|query
operator|.
name|add
argument_list|(
name|clause
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|drillDownDims
operator|.
name|put
argument_list|(
name|getDim
argument_list|(
name|clause
argument_list|)
argument_list|,
name|drillDownDims
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getDim
name|String
name|getDim
parameter_list|(
name|Query
name|clause
parameter_list|)
block|{
assert|assert
name|clause
operator|instanceof
name|ConstantScoreQuery
assert|;
name|clause
operator|=
operator|(
operator|(
name|ConstantScoreQuery
operator|)
name|clause
operator|)
operator|.
name|getQuery
argument_list|()
expr_stmt|;
assert|assert
name|clause
operator|instanceof
name|TermQuery
operator|||
name|clause
operator|instanceof
name|BooleanQuery
assert|;
name|String
name|term
decl_stmt|;
if|if
condition|(
name|clause
operator|instanceof
name|TermQuery
condition|)
block|{
name|term
operator|=
operator|(
operator|(
name|TermQuery
operator|)
name|clause
operator|)
operator|.
name|getTerm
argument_list|()
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|term
operator|=
operator|(
call|(
name|TermQuery
call|)
argument_list|(
operator|(
name|BooleanQuery
operator|)
name|clause
argument_list|)
operator|.
name|getClauses
argument_list|()
index|[
literal|0
index|]
operator|.
name|getQuery
argument_list|()
operator|)
operator|.
name|getTerm
argument_list|()
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
return|return
name|term
operator|.
name|split
argument_list|(
name|Pattern
operator|.
name|quote
argument_list|(
name|Character
operator|.
name|toString
argument_list|(
name|fip
operator|.
name|getFacetDelimChar
argument_list|()
argument_list|)
argument_list|)
argument_list|,
literal|2
argument_list|)
index|[
literal|0
index|]
return|;
block|}
comment|/**    * Creates a new {@link DrillDownQuery} without a base query,     * to perform a pure browsing query (equivalent to using    * {@link MatchAllDocsQuery} as base).    */
DECL|method|DrillDownQuery
specifier|public
name|DrillDownQuery
parameter_list|(
name|FacetIndexingParams
name|fip
parameter_list|)
block|{
name|this
argument_list|(
name|fip
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new {@link DrillDownQuery} over the given base query. Can be    * {@code null}, in which case the result {@link Query} from    * {@link #rewrite(IndexReader)} will be a pure browsing query, filtering on    * the added categories only.    */
DECL|method|DrillDownQuery
specifier|public
name|DrillDownQuery
parameter_list|(
name|FacetIndexingParams
name|fip
parameter_list|,
name|Query
name|baseQuery
parameter_list|)
block|{
name|query
operator|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// disable coord
if|if
condition|(
name|baseQuery
operator|!=
literal|null
condition|)
block|{
name|query
operator|.
name|add
argument_list|(
name|baseQuery
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|fip
operator|=
name|fip
expr_stmt|;
block|}
comment|/**    * Adds one dimension of drill downs; if you pass multiple values they are    * OR'd, and then the entire dimension is AND'd against the base query.    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|CategoryPath
modifier|...
name|paths
parameter_list|)
block|{
name|Query
name|q
decl_stmt|;
if|if
condition|(
name|paths
index|[
literal|0
index|]
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"all CategoryPaths must have length> 0"
argument_list|)
throw|;
block|}
name|String
name|dim
init|=
name|paths
index|[
literal|0
index|]
operator|.
name|components
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|drillDownDims
operator|.
name|containsKey
argument_list|(
name|dim
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"dimension '"
operator|+
name|dim
operator|+
literal|"' was already added"
argument_list|)
throw|;
block|}
if|if
condition|(
name|paths
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|q
operator|=
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|(
name|fip
argument_list|,
name|paths
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
decl_stmt|;
comment|// disable coord
for|for
control|(
name|CategoryPath
name|cp
range|:
name|paths
control|)
block|{
if|if
condition|(
name|cp
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"all CategoryPaths must have length> 0"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|cp
operator|.
name|components
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
name|dim
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"multiple (OR'd) drill-down paths must be under same dimension; got '"
operator|+
name|dim
operator|+
literal|"' and '"
operator|+
name|cp
operator|.
name|components
index|[
literal|0
index|]
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|(
name|fip
argument_list|,
name|cp
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
name|q
operator|=
name|bq
expr_stmt|;
block|}
name|add
argument_list|(
name|dim
argument_list|,
name|q
argument_list|)
expr_stmt|;
block|}
comment|/** Expert: add a custom drill-down subQuery.  Use this    *  when you have a separate way to drill-down on the    *  dimension than the indexed facet ordinals. */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|String
name|dim
parameter_list|,
name|Query
name|subQuery
parameter_list|)
block|{
comment|// TODO: we should use FilteredQuery?
comment|// So scores of the drill-down query don't have an
comment|// effect:
specifier|final
name|ConstantScoreQuery
name|drillDownQuery
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|subQuery
argument_list|)
decl_stmt|;
name|drillDownQuery
operator|.
name|setBoost
argument_list|(
literal|0.0f
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
name|drillDownQuery
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|drillDownDims
operator|.
name|put
argument_list|(
name|dim
argument_list|,
name|drillDownDims
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|DrillDownQuery
name|clone
parameter_list|()
block|{
return|return
operator|new
name|DrillDownQuery
argument_list|(
name|fip
argument_list|,
name|query
argument_list|,
name|drillDownDims
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
return|return
name|prime
operator|*
name|result
operator|+
name|query
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|DrillDownQuery
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|DrillDownQuery
name|other
init|=
operator|(
name|DrillDownQuery
operator|)
name|obj
decl_stmt|;
return|return
name|query
operator|.
name|equals
argument_list|(
name|other
operator|.
name|query
argument_list|)
operator|&&
name|super
operator|.
name|equals
argument_list|(
name|other
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|r
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|query
operator|.
name|clauses
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// baseQuery given to the ctor was null + no drill-downs were added
comment|// note that if only baseQuery was given to the ctor, but no drill-down terms
comment|// is fine, since the rewritten query will be the original base query.
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"no base query or drill-down categories given"
argument_list|)
throw|;
block|}
return|return
name|query
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|query
operator|.
name|toString
argument_list|(
name|field
argument_list|)
return|;
block|}
DECL|method|getBooleanQuery
name|BooleanQuery
name|getBooleanQuery
parameter_list|()
block|{
return|return
name|query
return|;
block|}
DECL|method|getDims
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|getDims
parameter_list|()
block|{
return|return
name|drillDownDims
return|;
block|}
block|}
end_class
end_unit
