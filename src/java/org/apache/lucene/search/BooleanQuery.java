begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package
begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
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
name|Vector
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
begin_comment
comment|/** A Query that matches documents matching boolean combinations of other   queries, typically {@link TermQuery}s or {@link PhraseQuery}s.   */
end_comment
begin_class
DECL|class|BooleanQuery
specifier|final
specifier|public
class|class
name|BooleanQuery
extends|extends
name|Query
block|{
DECL|field|clauses
specifier|private
name|Vector
name|clauses
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
comment|/** Constructs an empty boolean query. */
DECL|method|BooleanQuery
specifier|public
name|BooleanQuery
parameter_list|()
block|{}
comment|/** Adds a clause to a boolean query.  Clauses may be:<ul><li><code>required</code> which means that documents which<i>do not</i>     match this sub-query will<it>not</it> match the boolean query;<li><code>prohibited</code> which means that documents which<i>do</i>     match this sub-query will<it>not</it> match the boolean query; or<li>neither, in which case matched documents are neither prohibited from     nor required to match the sub-query.</ul>     It is an error to specify a clause as both<code>required</code> and<code>prohibited</code>.     */
DECL|method|add
specifier|public
specifier|final
name|void
name|add
parameter_list|(
name|Query
name|query
parameter_list|,
name|boolean
name|required
parameter_list|,
name|boolean
name|prohibited
parameter_list|)
block|{
name|clauses
operator|.
name|addElement
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|query
argument_list|,
name|required
argument_list|,
name|prohibited
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Adds a clause to a boolean query. */
DECL|method|add
specifier|public
specifier|final
name|void
name|add
parameter_list|(
name|BooleanClause
name|clause
parameter_list|)
block|{
name|clauses
operator|.
name|addElement
argument_list|(
name|clause
argument_list|)
expr_stmt|;
block|}
DECL|method|prepare
name|void
name|prepare
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
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
name|BooleanClause
name|c
init|=
operator|(
name|BooleanClause
operator|)
name|clauses
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|c
operator|.
name|query
operator|.
name|prepare
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|sumOfSquaredWeights
specifier|final
name|float
name|sumOfSquaredWeights
parameter_list|(
name|Searcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|float
name|sum
init|=
literal|0.0f
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
name|BooleanClause
name|c
init|=
operator|(
name|BooleanClause
operator|)
name|clauses
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|c
operator|.
name|prohibited
condition|)
name|sum
operator|+=
name|c
operator|.
name|query
operator|.
name|sumOfSquaredWeights
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
comment|// sum sub-query weights
block|}
return|return
name|sum
return|;
block|}
DECL|method|normalize
specifier|final
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|)
block|{
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
name|BooleanClause
name|c
init|=
operator|(
name|BooleanClause
operator|)
name|clauses
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|c
operator|.
name|prohibited
condition|)
name|c
operator|.
name|query
operator|.
name|normalize
argument_list|(
name|norm
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|scorer
specifier|final
name|Scorer
name|scorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|clauses
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// optimize 1-term queries
name|BooleanClause
name|c
init|=
operator|(
name|BooleanClause
operator|)
name|clauses
operator|.
name|elementAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|c
operator|.
name|prohibited
condition|)
comment|// just return term scorer
return|return
name|c
operator|.
name|query
operator|.
name|scorer
argument_list|(
name|reader
argument_list|)
return|;
block|}
name|BooleanScorer
name|result
init|=
operator|new
name|BooleanScorer
argument_list|()
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
name|BooleanClause
name|c
init|=
operator|(
name|BooleanClause
operator|)
name|clauses
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Scorer
name|subScorer
init|=
name|c
operator|.
name|query
operator|.
name|scorer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|subScorer
operator|!=
literal|null
condition|)
name|result
operator|.
name|add
argument_list|(
name|subScorer
argument_list|,
name|c
operator|.
name|required
argument_list|,
name|c
operator|.
name|prohibited
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|c
operator|.
name|required
condition|)
return|return
literal|null
return|;
block|}
return|return
name|result
return|;
block|}
comment|/** Prints a user-readable version of this query. */
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
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
name|BooleanClause
name|c
init|=
operator|(
name|BooleanClause
operator|)
name|clauses
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|.
name|prohibited
condition|)
name|buffer
operator|.
name|append
argument_list|(
literal|"-"
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|c
operator|.
name|required
condition|)
name|buffer
operator|.
name|append
argument_list|(
literal|"+"
argument_list|)
expr_stmt|;
name|Query
name|subQuery
init|=
name|c
operator|.
name|query
decl_stmt|;
if|if
condition|(
name|subQuery
operator|instanceof
name|BooleanQuery
condition|)
block|{
comment|// wrap sub-bools in parens
name|BooleanQuery
name|bq
init|=
operator|(
name|BooleanQuery
operator|)
name|subQuery
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|c
operator|.
name|query
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
else|else
name|buffer
operator|.
name|append
argument_list|(
name|c
operator|.
name|query
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
operator|!=
name|clauses
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
name|buffer
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
