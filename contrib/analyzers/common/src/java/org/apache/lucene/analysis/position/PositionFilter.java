begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.position
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|position
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
name|TokenFilter
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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|Token
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PositionIncrementAttribute
import|;
end_import
begin_comment
comment|/** Set the positionIncrement of all tokens to the "positionIncrement",  * except the first return token which retains its original positionIncrement value.  * The default positionIncrement value is zero.  */
end_comment
begin_class
DECL|class|PositionFilter
specifier|public
class|class
name|PositionFilter
extends|extends
name|TokenFilter
block|{
comment|/** Position increment to assign to all but the first token - default = 0 */
DECL|field|positionIncrement
specifier|private
name|int
name|positionIncrement
init|=
literal|0
decl_stmt|;
comment|/** The first token must have non-zero positionIncrement **/
DECL|field|firstTokenPositioned
specifier|private
name|boolean
name|firstTokenPositioned
init|=
literal|false
decl_stmt|;
DECL|field|posIncrAtt
specifier|private
name|PositionIncrementAttribute
name|posIncrAtt
decl_stmt|;
comment|/**    * Constructs a PositionFilter that assigns a position increment of zero to    * all but the first token from the given input stream.    *     * @param input the input stream    */
DECL|method|PositionFilter
specifier|public
name|PositionFilter
parameter_list|(
specifier|final
name|TokenStream
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|posIncrAtt
operator|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a PositionFilter that assigns the given position increment to    * all but the first token from the given input stream.    *     * @param input the input stream    * @param positionIncrement position increment to assign to all but the first    *  token from the input stream    */
DECL|method|PositionFilter
specifier|public
name|PositionFilter
parameter_list|(
specifier|final
name|TokenStream
name|input
parameter_list|,
specifier|final
name|int
name|positionIncrement
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|positionIncrement
operator|=
name|positionIncrement
expr_stmt|;
block|}
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
if|if
condition|(
name|firstTokenPositioned
condition|)
block|{
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
name|positionIncrement
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|firstTokenPositioned
operator|=
literal|true
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/** @deprecated Will be removed in Lucene 3.0. This method is final, as it should    * not be overridden. Delegates to the backwards compatibility layer. */
DECL|method|next
specifier|public
specifier|final
name|Token
name|next
parameter_list|(
specifier|final
name|Token
name|reusableToken
parameter_list|)
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
return|return
name|super
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
return|;
block|}
comment|/** @deprecated Will be removed in Lucene 3.0. This method is final, as it should    * not be overridden. Delegates to the backwards compatibility layer. */
DECL|method|next
specifier|public
specifier|final
name|Token
name|next
parameter_list|()
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
return|return
name|super
operator|.
name|next
argument_list|()
return|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|firstTokenPositioned
operator|=
literal|false
expr_stmt|;
block|}
block|}
end_class
end_unit
