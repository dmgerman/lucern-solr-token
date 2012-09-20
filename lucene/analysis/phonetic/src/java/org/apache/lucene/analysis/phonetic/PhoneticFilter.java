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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|Encoder
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
comment|/**  * Create tokens for phonetic matches.  * @see<a href="http://commons.apache.org/codec/api-release/org/apache/commons/codec/language/package-summary.html">  * Apache Commons Codec</a>  */
end_comment
begin_class
DECL|class|PhoneticFilter
specifier|public
specifier|final
class|class
name|PhoneticFilter
extends|extends
name|TokenFilter
block|{
comment|/** true if encoded tokens should be added as synonyms */
DECL|field|inject
specifier|protected
name|boolean
name|inject
init|=
literal|true
decl_stmt|;
comment|/** phonetic encoder */
DECL|field|encoder
specifier|protected
name|Encoder
name|encoder
init|=
literal|null
decl_stmt|;
comment|/** captured state, non-null when<code>inject=true</code> and a token is buffered */
DECL|field|save
specifier|protected
name|State
name|save
init|=
literal|null
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
comment|/** Creates a PhoneticFilter with the specified encoder, and either    *  adding encoded forms as synonyms (<code>inject=true</code>) or    *  replacing them.    */
DECL|method|PhoneticFilter
specifier|public
name|PhoneticFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|Encoder
name|encoder
parameter_list|,
name|boolean
name|inject
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|encoder
operator|=
name|encoder
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
if|if
condition|(
name|save
operator|!=
literal|null
condition|)
block|{
comment|// clearAttributes();  // not currently necessary
name|restoreState
argument_list|(
name|save
argument_list|)
expr_stmt|;
name|save
operator|=
literal|null
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
comment|// pass through zero-length terms
if|if
condition|(
name|termAtt
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|true
return|;
name|String
name|value
init|=
name|termAtt
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|phonetic
init|=
literal|null
decl_stmt|;
try|try
block|{
name|String
name|v
init|=
name|encoder
operator|.
name|encode
argument_list|(
name|value
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
operator|!
name|value
operator|.
name|equals
argument_list|(
name|v
argument_list|)
condition|)
name|phonetic
operator|=
name|v
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignored
parameter_list|)
block|{}
comment|// just use the direct text
if|if
condition|(
name|phonetic
operator|==
literal|null
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
name|inject
condition|)
block|{
comment|// just modify this token
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|phonetic
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|// We need to return both the original and the phonetic tokens.
comment|// to avoid a orig=captureState() change_to_phonetic() saved=captureState()  restoreState(orig)
comment|// we return the phonetic alternative first
name|int
name|origOffset
init|=
name|posAtt
operator|.
name|getPositionIncrement
argument_list|()
decl_stmt|;
name|posAtt
operator|.
name|setPositionIncrement
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|save
operator|=
name|captureState
argument_list|()
expr_stmt|;
name|posAtt
operator|.
name|setPositionIncrement
argument_list|(
name|origOffset
argument_list|)
expr_stmt|;
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|phonetic
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
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
name|save
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class
end_unit
