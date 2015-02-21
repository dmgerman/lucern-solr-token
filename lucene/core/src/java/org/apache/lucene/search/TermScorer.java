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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|PostingsEnum
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
name|similarities
operator|.
name|Similarity
import|;
end_import
begin_comment
comment|/** Expert: A<code>Scorer</code> for documents matching a<code>Term</code>.  */
end_comment
begin_class
DECL|class|TermScorer
specifier|final
class|class
name|TermScorer
extends|extends
name|Scorer
block|{
DECL|field|postingsEnum
specifier|private
specifier|final
name|PostingsEnum
name|postingsEnum
decl_stmt|;
DECL|field|docScorer
specifier|private
specifier|final
name|Similarity
operator|.
name|SimScorer
name|docScorer
decl_stmt|;
comment|/**    * Construct a<code>TermScorer</code>.    *    * @param weight    *          The weight of the<code>Term</code> in the query.    * @param td    *          An iterator over the documents matching the<code>Term</code>.    * @param docScorer    *          The</code>Similarity.SimScorer</code> implementation    *          to be used for score computations.    */
DECL|method|TermScorer
name|TermScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|PostingsEnum
name|td
parameter_list|,
name|Similarity
operator|.
name|SimScorer
name|docScorer
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|)
expr_stmt|;
name|this
operator|.
name|docScorer
operator|=
name|docScorer
expr_stmt|;
name|this
operator|.
name|postingsEnum
operator|=
name|td
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|postingsEnum
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|postingsEnum
operator|.
name|freq
argument_list|()
return|;
block|}
comment|/**    * Advances to the next document matching the query.<br>    *    * @return the document matching the query or NO_MORE_DOCS if there are no more documents.    */
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|postingsEnum
operator|.
name|nextDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|docID
argument_list|()
operator|!=
name|NO_MORE_DOCS
assert|;
return|return
name|docScorer
operator|.
name|score
argument_list|(
name|postingsEnum
operator|.
name|docID
argument_list|()
argument_list|,
name|postingsEnum
operator|.
name|freq
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Advances to the first match beyond the current whose document number is    * greater than or equal to a given target.<br>    * The implementation uses {@link org.apache.lucene.index.PostingsEnum#advance(int)}.    *    * @param target    *          The target document number.    * @return the matching document or NO_MORE_DOCS if none exist.    */
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|postingsEnum
operator|.
name|advance
argument_list|(
name|target
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|postingsEnum
operator|.
name|cost
argument_list|()
return|;
block|}
comment|/** Returns a string representation of this<code>TermScorer</code>. */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"scorer("
operator|+
name|weight
operator|+
literal|")["
operator|+
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|"]"
return|;
block|}
block|}
end_class
end_unit
