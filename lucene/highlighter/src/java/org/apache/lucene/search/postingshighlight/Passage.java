begin_unit
begin_package
DECL|package|org.apache.lucene.search.postingshighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|postingshighlight
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|util
operator|.
name|ArrayUtil
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
name|RamUsageEstimator
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
name|SorterTemplate
import|;
end_import
begin_comment
comment|/**  * Represents a passage (typically a sentence of the document).   *<p>  * A passage contains {@link #getNumMatches} highlights from the query,  * and the offsets and query terms that correspond with each match.  * @lucene.experimental  */
end_comment
begin_class
DECL|class|Passage
specifier|public
specifier|final
class|class
name|Passage
block|{
DECL|field|startOffset
name|int
name|startOffset
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|endOffset
name|int
name|endOffset
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|score
name|float
name|score
init|=
literal|0.0f
decl_stmt|;
DECL|field|matchStarts
name|int
name|matchStarts
index|[]
init|=
operator|new
name|int
index|[
literal|8
index|]
decl_stmt|;
DECL|field|matchEnds
name|int
name|matchEnds
index|[]
init|=
operator|new
name|int
index|[
literal|8
index|]
decl_stmt|;
DECL|field|matchTerms
name|Term
name|matchTerms
index|[]
init|=
operator|new
name|Term
index|[
literal|8
index|]
decl_stmt|;
DECL|field|numMatches
name|int
name|numMatches
init|=
literal|0
decl_stmt|;
DECL|method|addMatch
name|void
name|addMatch
parameter_list|(
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|,
name|Term
name|term
parameter_list|)
block|{
assert|assert
name|startOffset
operator|>=
name|this
operator|.
name|startOffset
operator|&&
name|startOffset
operator|<=
name|this
operator|.
name|endOffset
assert|;
if|if
condition|(
name|numMatches
operator|==
name|matchStarts
operator|.
name|length
condition|)
block|{
name|matchStarts
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|matchStarts
argument_list|,
name|numMatches
operator|+
literal|1
argument_list|)
expr_stmt|;
name|matchEnds
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|matchEnds
argument_list|,
name|numMatches
operator|+
literal|1
argument_list|)
expr_stmt|;
name|Term
name|newMatchTerms
index|[]
init|=
operator|new
name|Term
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|numMatches
operator|+
literal|1
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|matchTerms
argument_list|,
literal|0
argument_list|,
name|newMatchTerms
argument_list|,
literal|0
argument_list|,
name|numMatches
argument_list|)
expr_stmt|;
name|matchTerms
operator|=
name|newMatchTerms
expr_stmt|;
block|}
name|matchStarts
index|[
name|numMatches
index|]
operator|=
name|startOffset
expr_stmt|;
name|matchEnds
index|[
name|numMatches
index|]
operator|=
name|endOffset
expr_stmt|;
name|matchTerms
index|[
name|numMatches
index|]
operator|=
name|term
expr_stmt|;
name|numMatches
operator|++
expr_stmt|;
block|}
DECL|method|sort
name|void
name|sort
parameter_list|()
block|{
specifier|final
name|int
name|starts
index|[]
init|=
name|matchStarts
decl_stmt|;
specifier|final
name|int
name|ends
index|[]
init|=
name|matchEnds
decl_stmt|;
specifier|final
name|Term
name|terms
index|[]
init|=
name|matchTerms
decl_stmt|;
operator|new
name|SorterTemplate
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|swap
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
name|int
name|temp
init|=
name|starts
index|[
name|i
index|]
decl_stmt|;
name|starts
index|[
name|i
index|]
operator|=
name|starts
index|[
name|j
index|]
expr_stmt|;
name|starts
index|[
name|j
index|]
operator|=
name|temp
expr_stmt|;
name|temp
operator|=
name|ends
index|[
name|i
index|]
expr_stmt|;
name|ends
index|[
name|i
index|]
operator|=
name|ends
index|[
name|j
index|]
expr_stmt|;
name|ends
index|[
name|j
index|]
operator|=
name|temp
expr_stmt|;
name|Term
name|tempTerm
init|=
name|terms
index|[
name|i
index|]
decl_stmt|;
name|terms
index|[
name|i
index|]
operator|=
name|terms
index|[
name|j
index|]
expr_stmt|;
name|terms
index|[
name|j
index|]
operator|=
name|tempTerm
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|int
name|compare
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
comment|// TODO: java7 use Integer.compare(starts[i], starts[j])
return|return
name|Long
operator|.
name|signum
argument_list|(
operator|(
operator|(
name|long
operator|)
name|starts
index|[
name|i
index|]
operator|)
operator|-
name|starts
index|[
name|j
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setPivot
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|pivot
operator|=
name|starts
index|[
name|i
index|]
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|int
name|comparePivot
parameter_list|(
name|int
name|j
parameter_list|)
block|{
comment|// TODO: java7 use Integer.compare(pivot, starts[j])
return|return
name|Long
operator|.
name|signum
argument_list|(
operator|(
operator|(
name|long
operator|)
name|pivot
operator|)
operator|-
name|starts
index|[
name|j
index|]
argument_list|)
return|;
block|}
name|int
name|pivot
decl_stmt|;
block|}
operator|.
name|mergeSort
argument_list|(
literal|0
argument_list|,
name|numMatches
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|reset
name|void
name|reset
parameter_list|()
block|{
name|startOffset
operator|=
name|endOffset
operator|=
operator|-
literal|1
expr_stmt|;
name|score
operator|=
literal|0.0f
expr_stmt|;
name|numMatches
operator|=
literal|0
expr_stmt|;
block|}
comment|/**    * Start offset of this passage.    * @return start index (inclusive) of the passage in the     *         original content: always&gt;= 0.    */
DECL|method|getStartOffset
specifier|public
name|int
name|getStartOffset
parameter_list|()
block|{
return|return
name|startOffset
return|;
block|}
comment|/**    * End offset of this passage.    * @return end index (exclusive) of the passage in the     *         original content: always&gt;= {@link #getStartOffset()}    */
DECL|method|getEndOffset
specifier|public
name|int
name|getEndOffset
parameter_list|()
block|{
return|return
name|endOffset
return|;
block|}
comment|/**    * Passage's score.    */
DECL|method|getScore
specifier|public
name|float
name|getScore
parameter_list|()
block|{
return|return
name|score
return|;
block|}
comment|/**    * Number of term matches available in     * {@link #getMatchStarts}, {@link #getMatchEnds},     * {@link #getMatchTerms}    */
DECL|method|getNumMatches
specifier|public
name|int
name|getNumMatches
parameter_list|()
block|{
return|return
name|numMatches
return|;
block|}
comment|/**    * Start offsets of the term matches, in increasing order.    *<p>    * Only {@link #getNumMatches} are valid. Note that these    * offsets are absolute (not relative to {@link #getStartOffset()}).    */
DECL|method|getMatchStarts
specifier|public
name|int
index|[]
name|getMatchStarts
parameter_list|()
block|{
return|return
name|matchStarts
return|;
block|}
comment|/**    * End offsets of the term matches, corresponding with {@link #getMatchStarts}.     *<p>    * Only {@link #getNumMatches} are valid. Note that its possible that an end offset     * could exceed beyond the bounds of the passage ({@link #getEndOffset()}), if the     * Analyzer produced a term which spans a passage boundary.    */
DECL|method|getMatchEnds
specifier|public
name|int
index|[]
name|getMatchEnds
parameter_list|()
block|{
return|return
name|matchEnds
return|;
block|}
comment|/**    * Term of the matches, corresponding with {@link #getMatchStarts()}.    *<p>    * Only {@link #getNumMatches()} are valid.    */
DECL|method|getMatchTerms
specifier|public
name|Term
index|[]
name|getMatchTerms
parameter_list|()
block|{
return|return
name|matchTerms
return|;
block|}
block|}
end_class
end_unit
