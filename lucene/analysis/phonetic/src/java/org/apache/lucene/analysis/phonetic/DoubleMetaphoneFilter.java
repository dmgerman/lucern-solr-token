begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.phonetic
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|phonetic
package|;
end_package
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
name|LinkedList
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|language
operator|.
name|DoubleMetaphone
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
name|tokenattributes
operator|.
name|CharTermAttribute
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
comment|/**  * Filter for DoubleMetaphone (supporting secondary codes)  */
end_comment
begin_class
DECL|class|DoubleMetaphoneFilter
specifier|public
specifier|final
class|class
name|DoubleMetaphoneFilter
extends|extends
name|TokenFilter
block|{
DECL|field|TOKEN_TYPE
specifier|private
specifier|static
specifier|final
name|String
name|TOKEN_TYPE
init|=
literal|"DoubleMetaphone"
decl_stmt|;
DECL|field|remainingTokens
specifier|private
specifier|final
name|LinkedList
argument_list|<
name|State
argument_list|>
name|remainingTokens
init|=
operator|new
name|LinkedList
argument_list|<
name|State
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|encoder
specifier|private
specifier|final
name|DoubleMetaphone
name|encoder
init|=
operator|new
name|DoubleMetaphone
argument_list|()
decl_stmt|;
DECL|field|inject
specifier|private
specifier|final
name|boolean
name|inject
decl_stmt|;
DECL|field|termAtt
specifier|private
specifier|final
name|CharTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|posAtt
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posAtt
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Creates a DoubleMetaphoneFilter with the specified maximum code length,     *  and either adding encoded forms as synonyms (<code>inject=true</code>) or    *  replacing them.    */
DECL|method|DoubleMetaphoneFilter
specifier|public
name|DoubleMetaphoneFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|int
name|maxCodeLength
parameter_list|,
name|boolean
name|inject
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|encoder
operator|.
name|setMaxCodeLen
argument_list|(
name|maxCodeLength
argument_list|)
expr_stmt|;
name|this
operator|.
name|inject
operator|=
name|inject
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
operator|!
name|remainingTokens
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// clearAttributes();  // not currently necessary
name|restoreState
argument_list|(
name|remainingTokens
operator|.
name|removeFirst
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
return|return
literal|false
return|;
name|int
name|len
init|=
name|termAtt
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|len
operator|==
literal|0
condition|)
return|return
literal|true
return|;
comment|// pass through zero length terms
name|int
name|firstAlternativeIncrement
init|=
name|inject
condition|?
literal|0
else|:
name|posAtt
operator|.
name|getPositionIncrement
argument_list|()
decl_stmt|;
name|String
name|v
init|=
name|termAtt
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|primaryPhoneticValue
init|=
name|encoder
operator|.
name|doubleMetaphone
argument_list|(
name|v
argument_list|)
decl_stmt|;
name|String
name|alternatePhoneticValue
init|=
name|encoder
operator|.
name|doubleMetaphone
argument_list|(
name|v
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// a flag to lazily save state if needed... this avoids a save/restore when only
comment|// one token will be generated.
name|boolean
name|saveState
init|=
name|inject
decl_stmt|;
if|if
condition|(
name|primaryPhoneticValue
operator|!=
literal|null
operator|&&
name|primaryPhoneticValue
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
operator|!
name|primaryPhoneticValue
operator|.
name|equals
argument_list|(
name|v
argument_list|)
condition|)
block|{
if|if
condition|(
name|saveState
condition|)
block|{
name|remainingTokens
operator|.
name|addLast
argument_list|(
name|captureState
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|posAtt
operator|.
name|setPositionIncrement
argument_list|(
name|firstAlternativeIncrement
argument_list|)
expr_stmt|;
name|firstAlternativeIncrement
operator|=
literal|0
expr_stmt|;
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|primaryPhoneticValue
argument_list|)
expr_stmt|;
name|saveState
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|alternatePhoneticValue
operator|!=
literal|null
operator|&&
name|alternatePhoneticValue
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
operator|!
name|alternatePhoneticValue
operator|.
name|equals
argument_list|(
name|primaryPhoneticValue
argument_list|)
operator|&&
operator|!
name|primaryPhoneticValue
operator|.
name|equals
argument_list|(
name|v
argument_list|)
condition|)
block|{
if|if
condition|(
name|saveState
condition|)
block|{
name|remainingTokens
operator|.
name|addLast
argument_list|(
name|captureState
argument_list|()
argument_list|)
expr_stmt|;
name|saveState
operator|=
literal|false
expr_stmt|;
block|}
name|posAtt
operator|.
name|setPositionIncrement
argument_list|(
name|firstAlternativeIncrement
argument_list|)
expr_stmt|;
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|alternatePhoneticValue
argument_list|)
expr_stmt|;
name|saveState
operator|=
literal|true
expr_stmt|;
block|}
comment|// Just one token to return, so no need to capture/restore
comment|// any state, simply return it.
if|if
condition|(
name|remainingTokens
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|saveState
condition|)
block|{
name|remainingTokens
operator|.
name|addLast
argument_list|(
name|captureState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|input
operator|.
name|reset
argument_list|()
expr_stmt|;
name|remainingTokens
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
