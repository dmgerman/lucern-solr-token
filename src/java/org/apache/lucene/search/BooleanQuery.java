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
comment|/** Adds a clause to a boolean query.  Clauses may be:<ul><li><code>required</code> which means that documents which<i>do not</i>     match this sub-query will<i>not</i> match the boolean query;<li><code>prohibited</code> which means that documents which<i>do</i>     match this sub-query will<i>not</i> match the boolean query; or<li>neither, in which case matched documents are neither prohibited from     nor required to match the sub-query.</ul>     It is an error to specify a clause as both<code>required</code> and<code>prohibited</code>.     */
DECL|method|add
specifier|public
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
comment|/** Returns the set of clauses in this query. */
DECL|method|getClauses
specifier|public
name|BooleanClause
index|[]
name|getClauses
parameter_list|()
block|{
return|return
operator|(
name|BooleanClause
index|[]
operator|)
name|clauses
operator|.
name|toArray
argument_list|(
operator|new
name|BooleanClause
index|[
literal|0
index|]
argument_list|)
return|;
block|}
DECL|class|BooleanWeight
specifier|private
class|class
name|BooleanWeight
implements|implements
name|Weight
block|{
DECL|field|searcher
specifier|private
name|Searcher
name|searcher
decl_stmt|;
DECL|field|norm
specifier|private
name|float
name|norm
decl_stmt|;
DECL|field|weights
specifier|private
name|Vector
name|weights
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
DECL|method|BooleanWeight
specifier|public
name|BooleanWeight
parameter_list|(
name|Searcher
name|searcher
parameter_list|)
block|{
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
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
name|weights
operator|.
name|add
argument_list|(
name|c
operator|.
name|query
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|BooleanQuery
operator|.
name|this
return|;
block|}
DECL|method|getValue
specifier|public
name|float
name|getValue
parameter_list|()
block|{
return|return
name|getBoost
argument_list|()
return|;
block|}
DECL|method|sumOfSquaredWeights
specifier|public
name|float
name|sumOfSquaredWeights
parameter_list|()
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
name|weights
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
name|Weight
name|w
init|=
operator|(
name|Weight
operator|)
name|weights
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
name|w
operator|.
name|sumOfSquaredWeights
argument_list|()
expr_stmt|;
comment|// sum sub weights
block|}
name|sum
operator|*=
name|getBoost
argument_list|()
operator|*
name|getBoost
argument_list|()
expr_stmt|;
comment|// boost each sub-weight
return|return
name|sum
return|;
block|}
DECL|method|normalize
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|)
block|{
name|norm
operator|*=
name|getBoost
argument_list|()
expr_stmt|;
comment|// incorporate boost
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|weights
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
name|Weight
name|w
init|=
operator|(
name|Weight
operator|)
name|weights
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
name|w
operator|.
name|normalize
argument_list|(
name|norm
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|BooleanScorer
name|result
init|=
operator|new
name|BooleanScorer
argument_list|(
name|searcher
operator|.
name|getSimilarity
argument_list|()
argument_list|)
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
name|weights
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
literal|0
argument_list|)
decl_stmt|;
name|Weight
name|w
init|=
operator|(
name|Weight
operator|)
name|weights
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Scorer
name|subScorer
init|=
name|w
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
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|()
throws|throws
name|IOException
block|{
name|Explanation
name|result
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|result
operator|.
name|setDescription
argument_list|(
literal|"boost("
operator|+
name|getQuery
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|result
operator|.
name|setValue
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
DECL|method|createWeight
specifier|protected
name|Weight
name|createWeight
parameter_list|(
name|Searcher
name|searcher
parameter_list|)
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
comment|// optimize 1-clause queries
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
comment|// just return clause weight
return|return
name|c
operator|.
name|query
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|)
return|;
block|}
return|return
operator|new
name|BooleanWeight
argument_list|(
name|searcher
argument_list|)
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
if|if
condition|(
name|getBoost
argument_list|()
operator|!=
literal|1.0
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|getBoost
argument_list|()
operator|!=
literal|1.0
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|")^"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|getBoost
argument_list|()
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
comment|/** Returns true iff<code>o</code> is equal to this. */
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
operator|(
name|o
operator|instanceof
name|BooleanQuery
operator|)
condition|)
return|return
literal|false
return|;
name|BooleanQuery
name|other
init|=
operator|(
name|BooleanQuery
operator|)
name|o
decl_stmt|;
return|return
operator|(
name|this
operator|.
name|getBoost
argument_list|()
operator|==
name|other
operator|.
name|getBoost
argument_list|()
operator|)
operator|&&
name|this
operator|.
name|clauses
operator|.
name|equals
argument_list|(
name|other
operator|.
name|clauses
argument_list|)
return|;
block|}
comment|/** Returns a hash code value for this object.*/
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|getBoost
argument_list|()
argument_list|)
operator|^
name|clauses
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class
end_unit
