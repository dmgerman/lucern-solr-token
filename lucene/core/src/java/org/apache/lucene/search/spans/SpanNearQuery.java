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
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Set
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
name|LeafReaderContext
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
name|index
operator|.
name|TermContext
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
name|Terms
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
name|IndexSearcher
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
name|util
operator|.
name|ToStringUtils
import|;
end_import
begin_comment
comment|/** Matches spans which are near one another.  One can specify<i>slop</i>, the  * maximum number of intervening unmatched positions, as well as whether  * matches are required to be in-order.  */
end_comment
begin_class
DECL|class|SpanNearQuery
specifier|public
class|class
name|SpanNearQuery
extends|extends
name|SpanQuery
implements|implements
name|Cloneable
block|{
DECL|field|clauses
specifier|protected
name|List
argument_list|<
name|SpanQuery
argument_list|>
name|clauses
decl_stmt|;
DECL|field|slop
specifier|protected
name|int
name|slop
decl_stmt|;
DECL|field|inOrder
specifier|protected
name|boolean
name|inOrder
decl_stmt|;
DECL|field|field
specifier|protected
name|String
name|field
decl_stmt|;
comment|/** Construct a SpanNearQuery.  Matches spans matching a span from each    * clause, with up to<code>slop</code> total unmatched positions between    * them.    *<br>When<code>inOrder</code> is true, the spans from each clause    * must be in the same order as in<code>clauses</code> and must be non-overlapping.    *<br>When<code>inOrder</code> is false, the spans from each clause    * need not be ordered and may overlap.    * @param clausesIn the clauses to find near each other, in the same field, at least 2.    * @param slop The slop value    * @param inOrder true if order is important    */
DECL|method|SpanNearQuery
specifier|public
name|SpanNearQuery
parameter_list|(
name|SpanQuery
index|[]
name|clausesIn
parameter_list|,
name|int
name|slop
parameter_list|,
name|boolean
name|inOrder
parameter_list|)
block|{
name|this
operator|.
name|clauses
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|clausesIn
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|SpanQuery
name|clause
range|:
name|clausesIn
control|)
block|{
if|if
condition|(
name|this
operator|.
name|field
operator|==
literal|null
condition|)
block|{
comment|// check field
name|this
operator|.
name|field
operator|=
name|clause
operator|.
name|getField
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|clause
operator|.
name|getField
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|clause
operator|.
name|getField
argument_list|()
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Clauses must have same field."
argument_list|)
throw|;
block|}
name|this
operator|.
name|clauses
operator|.
name|add
argument_list|(
name|clause
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|slop
operator|=
name|slop
expr_stmt|;
name|this
operator|.
name|inOrder
operator|=
name|inOrder
expr_stmt|;
block|}
comment|/** Return the clauses whose spans are matched. */
DECL|method|getClauses
specifier|public
name|SpanQuery
index|[]
name|getClauses
parameter_list|()
block|{
return|return
name|clauses
operator|.
name|toArray
argument_list|(
operator|new
name|SpanQuery
index|[
name|clauses
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/** Return the maximum number of intervening unmatched positions permitted.*/
DECL|method|getSlop
specifier|public
name|int
name|getSlop
parameter_list|()
block|{
return|return
name|slop
return|;
block|}
comment|/** Return true if matches are required to be in-order.*/
DECL|method|isInOrder
specifier|public
name|boolean
name|isInOrder
parameter_list|()
block|{
return|return
name|inOrder
return|;
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
name|field
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
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"spanNear(["
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|SpanQuery
argument_list|>
name|i
init|=
name|clauses
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|SpanQuery
name|clause
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|clause
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|"], "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|slop
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|inOrder
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|SpanWeight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|SpanWeight
argument_list|>
name|subWeights
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SpanQuery
name|q
range|:
name|clauses
control|)
block|{
name|subWeights
operator|.
name|add
argument_list|(
name|q
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|SpanNearWeight
argument_list|(
name|subWeights
argument_list|,
name|searcher
argument_list|,
name|needsScores
condition|?
name|getTermContexts
argument_list|(
name|subWeights
argument_list|)
else|:
literal|null
argument_list|)
return|;
block|}
DECL|class|SpanNearWeight
specifier|public
class|class
name|SpanNearWeight
extends|extends
name|SpanWeight
block|{
DECL|field|subWeights
specifier|final
name|List
argument_list|<
name|SpanWeight
argument_list|>
name|subWeights
decl_stmt|;
DECL|method|SpanNearWeight
specifier|public
name|SpanNearWeight
parameter_list|(
name|List
argument_list|<
name|SpanWeight
argument_list|>
name|subWeights
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|,
name|Map
argument_list|<
name|Term
argument_list|,
name|TermContext
argument_list|>
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|SpanNearQuery
operator|.
name|this
argument_list|,
name|searcher
argument_list|,
name|terms
argument_list|)
expr_stmt|;
name|this
operator|.
name|subWeights
operator|=
name|subWeights
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|extractTermContexts
specifier|public
name|void
name|extractTermContexts
parameter_list|(
name|Map
argument_list|<
name|Term
argument_list|,
name|TermContext
argument_list|>
name|contexts
parameter_list|)
block|{
for|for
control|(
name|SpanWeight
name|w
range|:
name|subWeights
control|)
block|{
name|w
operator|.
name|extractTermContexts
argument_list|(
name|contexts
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getSpans
specifier|public
name|Spans
name|getSpans
parameter_list|(
specifier|final
name|LeafReaderContext
name|context
parameter_list|,
name|Postings
name|requiredPostings
parameter_list|)
throws|throws
name|IOException
block|{
name|Terms
name|terms
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
comment|// field does not exist
block|}
name|ArrayList
argument_list|<
name|Spans
argument_list|>
name|subSpans
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|clauses
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|SpanWeight
name|w
range|:
name|subWeights
control|)
block|{
name|Spans
name|subSpan
init|=
name|w
operator|.
name|getSpans
argument_list|(
name|context
argument_list|,
name|requiredPostings
argument_list|)
decl_stmt|;
if|if
condition|(
name|subSpan
operator|!=
literal|null
condition|)
block|{
name|subSpans
operator|.
name|add
argument_list|(
name|subSpan
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|null
return|;
comment|// all required
block|}
block|}
comment|// all NearSpans require at least two subSpans
return|return
operator|(
operator|!
name|inOrder
operator|)
condition|?
operator|new
name|NearSpansUnordered
argument_list|(
name|SpanNearQuery
operator|.
name|this
argument_list|,
name|subSpans
argument_list|)
else|:
operator|new
name|NearSpansOrdered
argument_list|(
name|SpanNearQuery
operator|.
name|this
argument_list|,
name|subSpans
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
for|for
control|(
name|SpanWeight
name|w
range|:
name|subWeights
control|)
block|{
name|w
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
block|}
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
name|SpanNearQuery
name|clone
init|=
literal|null
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
name|clauses
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SpanQuery
name|c
init|=
name|clauses
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|SpanQuery
name|query
init|=
operator|(
name|SpanQuery
operator|)
name|c
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|!=
name|c
condition|)
block|{
comment|// clause rewrote: must clone
if|if
condition|(
name|clone
operator|==
literal|null
condition|)
name|clone
operator|=
name|this
operator|.
name|clone
argument_list|()
expr_stmt|;
name|clone
operator|.
name|clauses
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|clone
operator|!=
literal|null
condition|)
block|{
return|return
name|clone
return|;
comment|// some clauses rewrote
block|}
else|else
block|{
return|return
name|this
return|;
comment|// no clauses rewrote
block|}
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|SpanNearQuery
name|clone
parameter_list|()
block|{
name|int
name|sz
init|=
name|clauses
operator|.
name|size
argument_list|()
decl_stmt|;
name|SpanQuery
index|[]
name|newClauses
init|=
operator|new
name|SpanQuery
index|[
name|sz
index|]
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
name|sz
condition|;
name|i
operator|++
control|)
block|{
name|newClauses
index|[
name|i
index|]
operator|=
operator|(
name|SpanQuery
operator|)
name|clauses
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
name|SpanNearQuery
name|spanNearQuery
init|=
operator|new
name|SpanNearQuery
argument_list|(
name|newClauses
argument_list|,
name|slop
argument_list|,
name|inOrder
argument_list|)
decl_stmt|;
name|spanNearQuery
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|spanNearQuery
return|;
block|}
comment|/** Returns true iff<code>o</code> is equal to this. */
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|SpanNearQuery
name|spanNearQuery
init|=
operator|(
name|SpanNearQuery
operator|)
name|o
decl_stmt|;
return|return
operator|(
name|inOrder
operator|==
name|spanNearQuery
operator|.
name|inOrder
operator|)
operator|&&
operator|(
name|slop
operator|==
name|spanNearQuery
operator|.
name|slop
operator|)
operator|&&
name|clauses
operator|.
name|equals
argument_list|(
name|spanNearQuery
operator|.
name|clauses
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
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|^=
name|clauses
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|+=
name|slop
expr_stmt|;
name|int
name|fac
init|=
literal|1
operator|+
operator|(
name|inOrder
condition|?
literal|8
else|:
literal|4
operator|)
decl_stmt|;
return|return
name|fac
operator|*
name|result
return|;
block|}
block|}
end_class
end_unit
