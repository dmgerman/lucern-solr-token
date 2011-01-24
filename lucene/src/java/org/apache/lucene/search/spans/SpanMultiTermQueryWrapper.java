begin_unit
begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|IndexReader
operator|.
name|AtomicReaderContext
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
name|MultiTermQuery
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
name|TopTermsRewrite
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
name|ScoringRewrite
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
begin_comment
comment|// javadocs only
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|PerReaderTermState
import|;
end_import
begin_comment
comment|/**  * Wraps any {@link MultiTermQuery} as a {@link SpanQuery},   * so it can be nested within other SpanQuery classes.  *<p>  * The query is rewritten by default to a {@link SpanOrQuery} containing  * the expanded terms, but this can be customized.   *<p>  * Example:  *<blockquote><pre>  * {@code  * WildcardQuery wildcard = new WildcardQuery(new Term("field", "bro?n"));  * SpanQuery spanWildcard = new SpanMultiTermQueryWrapper<WildcardQuery>(wildcard);  * // do something with spanWildcard, such as use it in a SpanFirstQuery  * }  *</pre></blockquote>  */
end_comment
begin_class
DECL|class|SpanMultiTermQueryWrapper
specifier|public
class|class
name|SpanMultiTermQueryWrapper
parameter_list|<
name|Q
extends|extends
name|MultiTermQuery
parameter_list|>
extends|extends
name|SpanQuery
block|{
DECL|field|query
specifier|protected
specifier|final
name|Q
name|query
decl_stmt|;
comment|/**    * Create a new SpanMultiTermQueryWrapper.     *     * @param query Query to wrap.    *<p>    * NOTE: This will call {@link MultiTermQuery#setRewriteMethod(MultiTermQuery.RewriteMethod)}    * on the wrapped<code>query</code>, changing its rewrite method to a suitable one for spans.    * Be sure to not change the rewrite method on the wrapped query afterwards! Doing so will    * throw {@link UnsupportedOperationException} on rewriting this query!    */
DECL|method|SpanMultiTermQueryWrapper
specifier|public
name|SpanMultiTermQueryWrapper
parameter_list|(
name|Q
name|query
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|MultiTermQuery
operator|.
name|RewriteMethod
name|method
init|=
name|query
operator|.
name|getRewriteMethod
argument_list|()
decl_stmt|;
if|if
condition|(
name|method
operator|instanceof
name|TopTermsRewrite
condition|)
block|{
specifier|final
name|int
name|pqsize
init|=
operator|(
operator|(
name|TopTermsRewrite
operator|)
name|method
operator|)
operator|.
name|getSize
argument_list|()
decl_stmt|;
name|setRewriteMethod
argument_list|(
operator|new
name|TopTermsSpanBooleanQueryRewrite
argument_list|(
name|pqsize
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setRewriteMethod
argument_list|(
name|SCORING_SPAN_QUERY_REWRITE
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Expert: returns the rewriteMethod    */
DECL|method|getRewriteMethod
specifier|public
specifier|final
name|SpanRewriteMethod
name|getRewriteMethod
parameter_list|()
block|{
specifier|final
name|MultiTermQuery
operator|.
name|RewriteMethod
name|m
init|=
name|query
operator|.
name|getRewriteMethod
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|m
operator|instanceof
name|SpanRewriteMethod
operator|)
condition|)
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"You can only use SpanMultiTermQueryWrapper with a suitable SpanRewriteMethod."
argument_list|)
throw|;
return|return
operator|(
name|SpanRewriteMethod
operator|)
name|m
return|;
block|}
comment|/**    * Expert: sets the rewrite method. This only makes sense    * to be a span rewrite method.    */
DECL|method|setRewriteMethod
specifier|public
specifier|final
name|void
name|setRewriteMethod
parameter_list|(
name|SpanRewriteMethod
name|rewriteMethod
parameter_list|)
block|{
name|query
operator|.
name|setRewriteMethod
argument_list|(
name|rewriteMethod
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSpans
specifier|public
name|Spans
name|getSpans
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Query should have been rewritten"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|query
operator|.
name|getField
argument_list|()
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
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"SpanMultiTermQueryWrapper("
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|query
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
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
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Query
name|q
init|=
name|query
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|q
operator|instanceof
name|SpanQuery
operator|)
condition|)
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"You can only use SpanMultiTermQueryWrapper with a suitable SpanRewriteMethod."
argument_list|)
throw|;
return|return
name|q
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
return|return
literal|31
operator|*
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
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
specifier|final
name|SpanMultiTermQueryWrapper
name|other
init|=
operator|(
name|SpanMultiTermQueryWrapper
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
return|;
block|}
comment|/** Abstract class that defines how the query is rewritten. */
DECL|class|SpanRewriteMethod
specifier|public
specifier|static
specifier|abstract
class|class
name|SpanRewriteMethod
extends|extends
name|MultiTermQuery
operator|.
name|RewriteMethod
block|{
annotation|@
name|Override
DECL|method|rewrite
specifier|public
specifier|abstract
name|SpanQuery
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|MultiTermQuery
name|query
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
comment|/**    * A rewrite method that first translates each term into a SpanTermQuery in a    * {@link Occur#SHOULD} clause in a BooleanQuery, and keeps the    * scores as computed by the query.    *     * @see #setRewriteMethod    */
DECL|field|SCORING_SPAN_QUERY_REWRITE
specifier|public
specifier|final
specifier|static
name|SpanRewriteMethod
name|SCORING_SPAN_QUERY_REWRITE
init|=
operator|new
name|SpanRewriteMethod
argument_list|()
block|{
specifier|private
specifier|final
name|ScoringRewrite
argument_list|<
name|SpanOrQuery
argument_list|>
name|delegate
init|=
operator|new
name|ScoringRewrite
argument_list|<
name|SpanOrQuery
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|SpanOrQuery
name|getTopLevelQuery
parameter_list|()
block|{
return|return
operator|new
name|SpanOrQuery
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|checkMaxClauseCount
parameter_list|(
name|int
name|count
parameter_list|)
block|{
comment|// we accept all terms as SpanOrQuery has no limits
block|}
annotation|@
name|Override
specifier|protected
name|void
name|addClause
parameter_list|(
name|SpanOrQuery
name|topLevel
parameter_list|,
name|Term
name|term
parameter_list|,
name|int
name|docCount
parameter_list|,
name|float
name|boost
parameter_list|,
name|PerReaderTermState
name|states
parameter_list|)
block|{
specifier|final
name|SpanTermQuery
name|q
init|=
operator|new
name|SpanTermQuery
argument_list|(
name|term
argument_list|)
decl_stmt|;
name|q
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|topLevel
operator|.
name|addClause
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
annotation|@
name|Override
specifier|public
name|SpanQuery
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|MultiTermQuery
name|query
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|,
name|query
argument_list|)
return|;
block|}
comment|// Make sure we are still a singleton even after deserializing
specifier|protected
name|Object
name|readResolve
parameter_list|()
block|{
return|return
name|SCORING_SPAN_QUERY_REWRITE
return|;
block|}
block|}
decl_stmt|;
comment|/**    * A rewrite method that first translates each term into a SpanTermQuery in a    * {@link Occur#SHOULD} clause in a BooleanQuery, and keeps the    * scores as computed by the query.    *     *<p>    * This rewrite method only uses the top scoring terms so it will not overflow    * the boolean max clause count.    *     * @see #setRewriteMethod    */
DECL|class|TopTermsSpanBooleanQueryRewrite
specifier|public
specifier|static
specifier|final
class|class
name|TopTermsSpanBooleanQueryRewrite
extends|extends
name|SpanRewriteMethod
block|{
DECL|field|delegate
specifier|private
specifier|final
name|TopTermsRewrite
argument_list|<
name|SpanOrQuery
argument_list|>
name|delegate
decl_stmt|;
comment|/**       * Create a TopTermsSpanBooleanQueryRewrite for       * at most<code>size</code> terms.      */
DECL|method|TopTermsSpanBooleanQueryRewrite
specifier|public
name|TopTermsSpanBooleanQueryRewrite
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|delegate
operator|=
operator|new
name|TopTermsRewrite
argument_list|<
name|SpanOrQuery
argument_list|>
argument_list|(
name|size
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|int
name|getMaxSize
parameter_list|()
block|{
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
annotation|@
name|Override
specifier|protected
name|SpanOrQuery
name|getTopLevelQuery
parameter_list|()
block|{
return|return
operator|new
name|SpanOrQuery
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|addClause
parameter_list|(
name|SpanOrQuery
name|topLevel
parameter_list|,
name|Term
name|term
parameter_list|,
name|int
name|docFreq
parameter_list|,
name|float
name|boost
parameter_list|,
name|PerReaderTermState
name|states
parameter_list|)
block|{
specifier|final
name|SpanTermQuery
name|q
init|=
operator|new
name|SpanTermQuery
argument_list|(
name|term
argument_list|)
decl_stmt|;
name|q
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|topLevel
operator|.
name|addClause
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
block|}
expr_stmt|;
block|}
comment|/** return the maximum priority queue size */
DECL|method|getSize
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getSize
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|SpanQuery
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|MultiTermQuery
name|query
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|,
name|query
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
return|return
literal|31
operator|*
name|delegate
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
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
specifier|final
name|TopTermsSpanBooleanQueryRewrite
name|other
init|=
operator|(
name|TopTermsSpanBooleanQueryRewrite
operator|)
name|obj
decl_stmt|;
return|return
name|delegate
operator|.
name|equals
argument_list|(
name|other
operator|.
name|delegate
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
