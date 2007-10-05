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
begin_comment
comment|/**  * Expert: Common scoring functionality for different types of queries.  *  *<p>  * A<code>Scorer</code> either iterates over documents matching a  * query in increasing order of doc Id, or provides an explanation of  * the score for a query for a given document.  *</p>  *<p>  * Document scores are computed using a given<code>Similarity</code>  * implementation.  *</p>  * @see BooleanQuery#setAllowDocsOutOfOrder  */
end_comment
begin_class
DECL|class|Scorer
specifier|public
specifier|abstract
class|class
name|Scorer
block|{
DECL|field|similarity
specifier|private
name|Similarity
name|similarity
decl_stmt|;
comment|/** Constructs a Scorer.    * @param similarity The<code>Similarity</code> implementation used by this scorer.    */
DECL|method|Scorer
specifier|protected
name|Scorer
parameter_list|(
name|Similarity
name|similarity
parameter_list|)
block|{
name|this
operator|.
name|similarity
operator|=
name|similarity
expr_stmt|;
block|}
comment|/** Returns the Similarity implementation used by this scorer. */
DECL|method|getSimilarity
specifier|public
name|Similarity
name|getSimilarity
parameter_list|()
block|{
return|return
name|this
operator|.
name|similarity
return|;
block|}
comment|/** Scores and collects all matching documents.    * @param hc The collector to which all matching documents are passed through    * {@link HitCollector#collect(int, float)}.    *<br>When this method is used the {@link #explain(int)} method should not be used.    */
DECL|method|score
specifier|public
name|void
name|score
parameter_list|(
name|HitCollector
name|hc
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
name|next
argument_list|()
condition|)
block|{
name|hc
operator|.
name|collect
argument_list|(
name|doc
argument_list|()
argument_list|,
name|score
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Expert: Collects matching documents in a range.  Hook for optimization.    * Note that {@link #next()} must be called once before this method is called    * for the first time.    * @param hc The collector to which all matching documents are passed through    * {@link HitCollector#collect(int, float)}.    * @param max Do not score documents past this.    * @return true if more matching documents may remain.    */
DECL|method|score
specifier|protected
name|boolean
name|score
parameter_list|(
name|HitCollector
name|hc
parameter_list|,
name|int
name|max
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
name|doc
argument_list|()
operator|<
name|max
condition|)
block|{
name|hc
operator|.
name|collect
argument_list|(
name|doc
argument_list|()
argument_list|,
name|score
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|next
argument_list|()
condition|)
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Advances to the document matching this Scorer with the lowest doc Id    * greater then the current value of {@link doc()} (or to the matching    * document with the lowest doc Id if next has never been called on    * this Scorer).    *    *<p>    * When this method is used the {@link #explain(int)} method should not    * be used.    *</p>    *    * @return true iff there is another document matching the query.    * @see BooleanQuery#setAllowDocsOutOfOrder    */
DECL|method|next
specifier|public
specifier|abstract
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns the current document number matching the query.    * Initially invalid, until {@link #next()} is called the first time.    */
DECL|method|doc
specifier|public
specifier|abstract
name|int
name|doc
parameter_list|()
function_decl|;
comment|/** Returns the score of the current document matching the query.    * Initially invalid, until {@link #next()} or {@link #skipTo(int)}    * is called the first time.    */
DECL|method|score
specifier|public
specifier|abstract
name|float
name|score
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Skips to the document matching this Scorer with the lowest doc Id    * greater than or equal to a given target.    *    *<p>    * The behavior of this method is undefined if the target specified is    * less then or equal to the current value of {@link #doc()}    *<p>    * Behaves as if written:    *<pre>    *   boolean skipTo(int target) {    *     do {    *       if (!next())    * 	     return false;    *     } while (target> doc());    *     return true;    *   }    *</pre>    * Most implementations are considerably more efficient than that.    *</p>    *    *<p>    * When this method is used the {@link #explain(int)} method should not    * be used.    *</p>    *    * @param target The target document number.    * @return true iff there is such a match.    * @see BooleanQuery#setAllowDocsOutOfOrder    */
DECL|method|skipTo
specifier|public
specifier|abstract
name|boolean
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns an explanation of the score for a document.    *<br>When this method is used, the {@link #next()}, {@link #skipTo(int)} and    * {@link #score(HitCollector)} methods should not be used.    * @param doc The document number for the explanation.    */
DECL|method|explain
specifier|public
specifier|abstract
name|Explanation
name|explain
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class
end_unit
