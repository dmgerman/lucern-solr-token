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
name|TermDocs
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
name|TermPositions
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
comment|/** A Query that matches documents containing a particular sequence of terms.   This may be combined with other terms with a {@link BooleanQuery}.   */
end_comment
begin_class
DECL|class|PhraseQuery
specifier|final
specifier|public
class|class
name|PhraseQuery
extends|extends
name|Query
block|{
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|field|terms
specifier|private
name|Vector
name|terms
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
DECL|field|idf
specifier|private
name|float
name|idf
init|=
literal|0.0f
decl_stmt|;
DECL|field|weight
specifier|private
name|float
name|weight
init|=
literal|0.0f
decl_stmt|;
DECL|field|slop
specifier|private
name|int
name|slop
init|=
literal|0
decl_stmt|;
comment|/** Constructs an empty phrase query. */
DECL|method|PhraseQuery
specifier|public
name|PhraseQuery
parameter_list|()
block|{   }
comment|/** Sets the number of other words permitted between words in query phrase.     If zero, then this is an exact phrase search.  For larger values this works     like a<code>WITHIN</code> or<code>NEAR</code> operator.<p>The slop is in fact an edit-distance, where the units correspond to     moves of terms in the query phrase out of position.  For example, to switch     the order of two words requires two moves (the first move places the words     atop one another), so to permit re-orderings of phrases, the slop must be     at least two.<p>More exact matches are scored higher than sloppier matches, thus search     results are sorted by exactness.<p>The slop is zero by default, requiring exact matches.*/
DECL|method|setSlop
specifier|public
specifier|final
name|void
name|setSlop
parameter_list|(
name|int
name|s
parameter_list|)
block|{
name|slop
operator|=
name|s
expr_stmt|;
block|}
comment|/** Returns the slop.  See setSlop(). */
DECL|method|getSlop
specifier|public
specifier|final
name|int
name|getSlop
parameter_list|()
block|{
return|return
name|slop
return|;
block|}
comment|/** Adds a term to the end of the query phrase. */
DECL|method|add
specifier|public
specifier|final
name|void
name|add
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
if|if
condition|(
name|terms
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
name|field
operator|=
name|term
operator|.
name|field
argument_list|()
expr_stmt|;
elseif|else
if|if
condition|(
name|term
operator|.
name|field
argument_list|()
operator|!=
name|field
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"All phrase terms must be in the same field: "
operator|+
name|term
argument_list|)
throw|;
name|terms
operator|.
name|addElement
argument_list|(
name|term
argument_list|)
expr_stmt|;
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|terms
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
comment|// sum term IDFs
name|idf
operator|+=
name|Similarity
operator|.
name|idf
argument_list|(
operator|(
name|Term
operator|)
name|terms
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|weight
operator|=
name|idf
operator|*
name|boost
expr_stmt|;
return|return
name|weight
operator|*
name|weight
return|;
comment|// square term weights
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
name|weight
operator|*=
name|norm
expr_stmt|;
comment|// normalize for query
name|weight
operator|*=
name|idf
expr_stmt|;
comment|// factor from document
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
name|terms
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
comment|// optimize zero-term case
return|return
literal|null
return|;
if|if
condition|(
name|terms
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// optimize one-term case
name|Term
name|term
init|=
operator|(
name|Term
operator|)
name|terms
operator|.
name|elementAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|TermDocs
name|docs
init|=
name|reader
operator|.
name|termDocs
argument_list|(
name|term
argument_list|)
decl_stmt|;
if|if
condition|(
name|docs
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
operator|new
name|TermScorer
argument_list|(
name|docs
argument_list|,
name|reader
operator|.
name|norms
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
argument_list|,
name|weight
argument_list|)
return|;
block|}
name|TermPositions
index|[]
name|tps
init|=
operator|new
name|TermPositions
index|[
name|terms
operator|.
name|size
argument_list|()
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
name|terms
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|TermPositions
name|p
init|=
name|reader
operator|.
name|termPositions
argument_list|(
operator|(
name|Term
operator|)
name|terms
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|tps
index|[
name|i
index|]
operator|=
name|p
expr_stmt|;
block|}
if|if
condition|(
name|slop
operator|==
literal|0
condition|)
comment|// optimize exact case
return|return
operator|new
name|ExactPhraseScorer
argument_list|(
name|tps
argument_list|,
name|reader
operator|.
name|norms
argument_list|(
name|field
argument_list|)
argument_list|,
name|weight
argument_list|)
return|;
else|else
return|return
operator|new
name|SloppyPhraseScorer
argument_list|(
name|tps
argument_list|,
name|slop
argument_list|,
name|reader
operator|.
name|norms
argument_list|(
name|field
argument_list|)
argument_list|,
name|weight
argument_list|)
return|;
block|}
comment|/** Prints a user-readable version of this query. */
DECL|method|toString
specifier|public
specifier|final
name|String
name|toString
parameter_list|(
name|String
name|f
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
operator|!
name|field
operator|.
name|equals
argument_list|(
name|f
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
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
name|terms
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
operator|(
operator|(
name|Term
operator|)
name|terms
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|!=
name|terms
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
name|buffer
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
if|if
condition|(
name|boost
operator|!=
literal|1.0f
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"^"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|Float
operator|.
name|toString
argument_list|(
name|boost
argument_list|)
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
