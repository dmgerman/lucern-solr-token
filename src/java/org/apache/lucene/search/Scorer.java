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
comment|/**  * Expert: Common scoring functionality for different types of queries.  *  *<p>  * A<code>Scorer</code> iterates over documents matching a  * query in increasing order of doc Id.  *</p>  *<p>  * Document scores are computed using a given<code>Similarity</code>  * implementation.  *</p>  *  *<p><b>NOTE</b>: The values Float.Nan,  * Float.NEGATIVE_INFINITY and Float.POSITIVE_INFINITY are  * not valid scores.  Certain collectors (eg {@link  * TopScoreDocCollector}) will not properly collect hits  * with these scores.  */
end_comment
begin_class
DECL|class|Scorer
specifier|public
specifier|abstract
class|class
name|Scorer
extends|extends
name|DocIdSetIterator
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
comment|/** Scores and collects all matching documents.    * @param collector The collector to which all matching documents are passed.    */
DECL|method|score
specifier|public
name|void
name|score
parameter_list|(
name|Collector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
name|collector
operator|.
name|setScorer
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|int
name|doc
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|nextDoc
argument_list|()
operator|)
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
name|collector
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Expert: Collects matching documents in a range. Hook for optimization.    * Note,<code>firstDocID</code> is added to ensure that {@link #nextDoc()}    * was called before this method.    *     * @param collector    *          The collector to which all matching documents are passed.    * @param max    *          Do not score documents past this.    * @param firstDocID    *          The first document ID (ensures {@link #nextDoc()} is called before    *          this method.    * @return true if more matching documents may remain.    */
DECL|method|score
specifier|protected
name|boolean
name|score
parameter_list|(
name|Collector
name|collector
parameter_list|,
name|int
name|max
parameter_list|,
name|int
name|firstDocID
parameter_list|)
throws|throws
name|IOException
block|{
name|collector
operator|.
name|setScorer
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|int
name|doc
init|=
name|firstDocID
decl_stmt|;
while|while
condition|(
name|doc
operator|<
name|max
condition|)
block|{
name|collector
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
name|nextDoc
argument_list|()
expr_stmt|;
block|}
return|return
name|doc
operator|!=
name|NO_MORE_DOCS
return|;
block|}
comment|/** Returns the score of the current document matching the query.    * Initially invalid, until {@link #nextDoc()} or {@link #advance(int)}    * is called the first time, or when called from within    * {@link Collector#collect}.    */
DECL|method|score
specifier|public
specifier|abstract
name|float
name|score
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class
end_unit
