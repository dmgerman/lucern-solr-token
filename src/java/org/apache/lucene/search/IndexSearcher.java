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
name|BitSet
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
name|store
operator|.
name|Directory
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
name|document
operator|.
name|Document
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
begin_comment
comment|/** Implements search over a single IndexReader.  *  *<p>Applications usually need only call the inherited {@link #search(Query)}  * or {@link #search(Query,Filter)} methods.  */
end_comment
begin_class
DECL|class|IndexSearcher
specifier|public
class|class
name|IndexSearcher
extends|extends
name|Searcher
implements|implements
name|Searchable
block|{
DECL|field|reader
name|IndexReader
name|reader
decl_stmt|;
comment|/** Creates a searcher searching the index in the named directory. */
DECL|method|IndexSearcher
specifier|public
name|IndexSearcher
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a searcher searching the index in the provided directory. */
DECL|method|IndexSearcher
specifier|public
name|IndexSearcher
parameter_list|(
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a searcher searching the provided index. */
DECL|method|IndexSearcher
specifier|public
name|IndexSearcher
parameter_list|(
name|IndexReader
name|r
parameter_list|)
block|{
name|reader
operator|=
name|r
expr_stmt|;
block|}
comment|/**    * Frees resources associated with this Searcher.    * Be careful not to call this method while you are still using objects    * like {@link Hits}.    */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Expert: Returns the number of documents containing<code>term</code>.    * Called by search code to compute term weights.    * @see IndexReader#docFreq(Term).    */
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|reader
operator|.
name|docFreq
argument_list|(
name|term
argument_list|)
return|;
block|}
comment|/** For use by {@link HitCollector} implementations. */
DECL|method|doc
specifier|public
name|Document
name|doc
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|reader
operator|.
name|document
argument_list|(
name|i
argument_list|)
return|;
block|}
comment|/** Expert: Returns one greater than the largest possible document number.    * Called by search code to compute term weights.    * @see IndexReader#maxDoc().    */
DECL|method|maxDoc
specifier|public
name|int
name|maxDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|reader
operator|.
name|maxDoc
argument_list|()
return|;
block|}
comment|/** Expert: Low-level search implementation.  Finds the top<code>n</code>    * hits for<code>query</code>, applying<code>filter</code> if non-null.    *    *<p>Called by {@link Hits}.    *    *<p>Applications should usually call {@link #search(Query)} or {@link    * #search(Query,Filter)} instead.    */
DECL|method|search
specifier|public
name|TopDocs
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|,
specifier|final
name|int
name|nDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|Scorer
name|scorer
init|=
name|query
operator|.
name|weight
argument_list|(
name|this
argument_list|)
operator|.
name|scorer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|==
literal|null
condition|)
return|return
operator|new
name|TopDocs
argument_list|(
literal|0
argument_list|,
operator|new
name|ScoreDoc
index|[
literal|0
index|]
argument_list|)
return|;
specifier|final
name|BitSet
name|bits
init|=
name|filter
operator|!=
literal|null
condition|?
name|filter
operator|.
name|bits
argument_list|(
name|reader
argument_list|)
else|:
literal|null
decl_stmt|;
specifier|final
name|HitQueue
name|hq
init|=
operator|new
name|HitQueue
argument_list|(
name|nDocs
argument_list|)
decl_stmt|;
specifier|final
name|int
index|[]
name|totalHits
init|=
operator|new
name|int
index|[
literal|1
index|]
decl_stmt|;
name|scorer
operator|.
name|score
argument_list|(
operator|new
name|HitCollector
argument_list|()
block|{
specifier|private
name|float
name|minScore
init|=
literal|0.0f
decl_stmt|;
specifier|public
specifier|final
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|score
parameter_list|)
block|{
if|if
condition|(
name|score
operator|>
literal|0.0f
operator|&&
comment|// ignore zeroed buckets
operator|(
name|bits
operator|==
literal|null
operator|||
name|bits
operator|.
name|get
argument_list|(
name|doc
argument_list|)
operator|)
condition|)
block|{
comment|// skip docs not in bits
name|totalHits
index|[
literal|0
index|]
operator|++
expr_stmt|;
if|if
condition|(
name|score
operator|>=
name|minScore
condition|)
block|{
name|hq
operator|.
name|put
argument_list|(
operator|new
name|ScoreDoc
argument_list|(
name|doc
argument_list|,
name|score
argument_list|)
argument_list|)
expr_stmt|;
comment|// update hit queue
if|if
condition|(
name|hq
operator|.
name|size
argument_list|()
operator|>
name|nDocs
condition|)
block|{
comment|// if hit queue overfull
name|hq
operator|.
name|pop
argument_list|()
expr_stmt|;
comment|// remove lowest in hit queue
name|minScore
operator|=
operator|(
operator|(
name|ScoreDoc
operator|)
name|hq
operator|.
name|top
argument_list|()
operator|)
operator|.
name|score
expr_stmt|;
comment|// reset minScore
block|}
block|}
block|}
block|}
block|}
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|scoreDocs
init|=
operator|new
name|ScoreDoc
index|[
name|hq
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
name|hq
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
comment|// put docs in array
name|scoreDocs
index|[
name|i
index|]
operator|=
operator|(
name|ScoreDoc
operator|)
name|hq
operator|.
name|pop
argument_list|()
expr_stmt|;
return|return
operator|new
name|TopDocs
argument_list|(
name|totalHits
index|[
literal|0
index|]
argument_list|,
name|scoreDocs
argument_list|)
return|;
block|}
comment|/** Lower-level search API.    *    *<p>{@link HitCollector#collect(int,float)} is called for every non-zero    * scoring document.    *    *<p>Applications should only use this if they need<i>all</i> of the    * matching documents.  The high-level search API ({@link    * Searcher#search(Query)}) is usually more efficient, as it skips    * non-high-scoring hits.    *    * @param query to match documents    * @param filter if non-null, a bitset used to eliminate some documents    * @param results to receive hits    */
DECL|method|search
specifier|public
name|void
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|,
specifier|final
name|HitCollector
name|results
parameter_list|)
throws|throws
name|IOException
block|{
name|HitCollector
name|collector
init|=
name|results
decl_stmt|;
if|if
condition|(
name|filter
operator|!=
literal|null
condition|)
block|{
specifier|final
name|BitSet
name|bits
init|=
name|filter
operator|.
name|bits
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|collector
operator|=
operator|new
name|HitCollector
argument_list|()
block|{
specifier|public
specifier|final
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|score
parameter_list|)
block|{
if|if
condition|(
name|bits
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
comment|// skip docs not in bits
name|results
operator|.
name|collect
argument_list|(
name|doc
argument_list|,
name|score
argument_list|)
expr_stmt|;
block|}
block|}
block|}
expr_stmt|;
block|}
name|Scorer
name|scorer
init|=
name|query
operator|.
name|weight
argument_list|(
name|this
argument_list|)
operator|.
name|scorer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|==
literal|null
condition|)
return|return;
name|scorer
operator|.
name|score
argument_list|(
name|collector
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|Query
name|original
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
name|query
init|=
name|original
decl_stmt|;
for|for
control|(
name|Query
name|rewrittenQuery
init|=
name|query
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
init|;
name|rewrittenQuery
operator|!=
name|query
condition|;
name|rewrittenQuery
operator|=
name|query
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
control|)
block|{
name|query
operator|=
name|rewrittenQuery
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|Query
name|query
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|query
operator|.
name|weight
argument_list|(
name|this
argument_list|)
operator|.
name|explain
argument_list|(
name|reader
argument_list|,
name|doc
argument_list|)
return|;
block|}
block|}
end_class
end_unit
