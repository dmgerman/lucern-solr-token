begin_unit
begin_package
DECL|package|org.apache.lucene.search.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|highlight
package|;
end_package
begin_comment
comment|/**  * Copyright 2002-2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Token
import|;
end_import
begin_comment
comment|/**  * One, or several overlapping tokens, along with the score(s) and the  * scope of the original text  * @author MAHarwood  */
end_comment
begin_class
DECL|class|TokenGroup
specifier|public
class|class
name|TokenGroup
block|{
DECL|field|MAX_NUM_TOKENS_PER_GROUP
specifier|private
specifier|static
specifier|final
name|int
name|MAX_NUM_TOKENS_PER_GROUP
init|=
literal|50
decl_stmt|;
DECL|field|tokens
name|Token
index|[]
name|tokens
init|=
operator|new
name|Token
index|[
name|MAX_NUM_TOKENS_PER_GROUP
index|]
decl_stmt|;
DECL|field|scores
name|float
index|[]
name|scores
init|=
operator|new
name|float
index|[
name|MAX_NUM_TOKENS_PER_GROUP
index|]
decl_stmt|;
DECL|field|numTokens
name|int
name|numTokens
init|=
literal|0
decl_stmt|;
DECL|field|startOffset
name|int
name|startOffset
init|=
literal|0
decl_stmt|;
DECL|field|endOffset
name|int
name|endOffset
init|=
literal|0
decl_stmt|;
DECL|method|addToken
name|void
name|addToken
parameter_list|(
name|Token
name|token
parameter_list|,
name|float
name|score
parameter_list|)
block|{
if|if
condition|(
name|numTokens
operator|==
literal|0
condition|)
block|{
name|startOffset
operator|=
name|token
operator|.
name|startOffset
argument_list|()
expr_stmt|;
name|endOffset
operator|=
name|token
operator|.
name|endOffset
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|startOffset
operator|=
name|Math
operator|.
name|min
argument_list|(
name|startOffset
argument_list|,
name|token
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|endOffset
operator|=
name|Math
operator|.
name|max
argument_list|(
name|endOffset
argument_list|,
name|token
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|tokens
index|[
name|numTokens
index|]
operator|=
name|token
expr_stmt|;
name|scores
index|[
name|numTokens
index|]
operator|=
name|score
expr_stmt|;
name|numTokens
operator|++
expr_stmt|;
block|}
DECL|method|isDistinct
name|boolean
name|isDistinct
parameter_list|(
name|Token
name|token
parameter_list|)
block|{
return|return
name|token
operator|.
name|startOffset
argument_list|()
operator|>
name|endOffset
return|;
block|}
DECL|method|clear
name|void
name|clear
parameter_list|()
block|{
name|numTokens
operator|=
literal|0
expr_stmt|;
block|}
comment|/** 	 *  	 * @param index a value between 0 and numTokens -1 	 * @return the "n"th token 	 */
DECL|method|getToken
specifier|public
name|Token
name|getToken
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|tokens
index|[
name|index
index|]
return|;
block|}
comment|/** 	 *  	 * @param index a value between 0 and numTokens -1 	 * @return the "n"th score 	 */
DECL|method|getScore
specifier|public
name|float
name|getScore
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|scores
index|[
name|index
index|]
return|;
block|}
comment|/** 	 * @return the end position in the original text 	 */
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
comment|/** 	 * @return the number of tokens in this group 	 */
DECL|method|getNumTokens
specifier|public
name|int
name|getNumTokens
parameter_list|()
block|{
return|return
name|numTokens
return|;
block|}
comment|/** 	 * @return the start position in the original text 	 */
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
comment|/** 	 * @return all tokens' scores summed up 	 */
DECL|method|getTotalScore
specifier|public
name|float
name|getTotalScore
parameter_list|()
block|{
name|float
name|total
init|=
literal|0
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
name|numTokens
condition|;
name|i
operator|++
control|)
block|{
name|total
operator|+=
name|scores
index|[
name|i
index|]
expr_stmt|;
block|}
return|return
name|total
return|;
block|}
block|}
end_class
end_unit
