begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
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
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|AttributeImpl
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
name|TermAttribute
import|;
end_import
begin_class
DECL|class|BaseTokenTestCase
specifier|public
specifier|abstract
class|class
name|BaseTokenTestCase
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|tsToString
specifier|public
specifier|static
name|String
name|tsToString
parameter_list|(
name|TokenStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|TermAttribute
name|termAtt
init|=
operator|(
name|TermAttribute
operator|)
name|in
operator|.
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|StringBuffer
name|out
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|in
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|in
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
if|if
condition|(
name|out
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|out
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
name|termAtt
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|out
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|assertTokEqual
specifier|public
name|void
name|assertTokEqual
parameter_list|(
name|List
comment|/*<Token>*/
name|a
parameter_list|,
name|List
comment|/*<Token>*/
name|b
parameter_list|)
block|{
name|assertTokEq
argument_list|(
name|a
argument_list|,
name|b
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertTokEq
argument_list|(
name|b
argument_list|,
name|a
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|assertTokEqualOff
specifier|public
name|void
name|assertTokEqualOff
parameter_list|(
name|List
comment|/*<Token>*/
name|a
parameter_list|,
name|List
comment|/*<Token>*/
name|b
parameter_list|)
block|{
name|assertTokEq
argument_list|(
name|a
argument_list|,
name|b
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTokEq
argument_list|(
name|b
argument_list|,
name|a
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|assertTokEq
specifier|private
name|void
name|assertTokEq
parameter_list|(
name|List
comment|/*<Token>*/
name|a
parameter_list|,
name|List
comment|/*<Token>*/
name|b
parameter_list|,
name|boolean
name|checkOff
parameter_list|)
block|{
name|int
name|pos
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|a
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Token
name|tok
init|=
operator|(
name|Token
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|pos
operator|+=
name|tok
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|tokAt
argument_list|(
name|b
argument_list|,
name|tok
operator|.
name|term
argument_list|()
argument_list|,
name|pos
argument_list|,
name|checkOff
condition|?
name|tok
operator|.
name|startOffset
argument_list|()
else|:
operator|-
literal|1
argument_list|,
name|checkOff
condition|?
name|tok
operator|.
name|endOffset
argument_list|()
else|:
operator|-
literal|1
argument_list|)
condition|)
block|{
name|fail
argument_list|(
name|a
operator|+
literal|"!="
operator|+
name|b
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|tokAt
specifier|public
name|boolean
name|tokAt
parameter_list|(
name|List
comment|/*<Token>*/
name|lst
parameter_list|,
name|String
name|val
parameter_list|,
name|int
name|tokPos
parameter_list|,
name|int
name|startOff
parameter_list|,
name|int
name|endOff
parameter_list|)
block|{
name|int
name|pos
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|lst
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Token
name|tok
init|=
operator|(
name|Token
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|pos
operator|+=
name|tok
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
if|if
condition|(
name|pos
operator|==
name|tokPos
operator|&&
name|tok
operator|.
name|term
argument_list|()
operator|.
name|equals
argument_list|(
name|val
argument_list|)
operator|&&
operator|(
name|startOff
operator|==
operator|-
literal|1
operator|||
name|tok
operator|.
name|startOffset
argument_list|()
operator|==
name|startOff
operator|)
operator|&&
operator|(
name|endOff
operator|==
operator|-
literal|1
operator|||
name|tok
operator|.
name|endOffset
argument_list|()
operator|==
name|endOff
operator|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/***    * Return a list of tokens according to a test string format:    * a b c  =>  returns List<Token> [a,b,c]    * a/b   => tokens a and b share the same spot (b.positionIncrement=0)    * a,3/b/c => a,b,c all share same position (a.positionIncrement=3, b.positionIncrement=0, c.positionIncrement=0)    * a,1,10,11  => "a" with positionIncrement=1, startOffset=10, endOffset=11    */
DECL|method|tokens
specifier|public
name|List
comment|/*<Token>*/
name|tokens
parameter_list|(
name|String
name|str
parameter_list|)
block|{
name|String
index|[]
name|arr
init|=
name|str
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
name|List
comment|/*<Token>*/
name|result
init|=
operator|new
name|ArrayList
comment|/*<Token>*/
argument_list|()
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
name|arr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
index|[]
name|toks
init|=
name|arr
index|[
name|i
index|]
operator|.
name|split
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|String
index|[]
name|params
init|=
name|toks
index|[
literal|0
index|]
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|int
name|posInc
decl_stmt|;
name|int
name|start
decl_stmt|;
name|int
name|end
decl_stmt|;
if|if
condition|(
name|params
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|posInc
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|params
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|posInc
operator|=
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|params
operator|.
name|length
operator|>
literal|2
condition|)
block|{
name|start
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|params
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|start
operator|=
literal|0
expr_stmt|;
block|}
if|if
condition|(
name|params
operator|.
name|length
operator|>
literal|3
condition|)
block|{
name|end
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|params
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|end
operator|=
name|start
operator|+
name|params
index|[
literal|0
index|]
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
name|Token
name|t
init|=
operator|new
name|Token
argument_list|(
name|params
index|[
literal|0
index|]
argument_list|,
name|start
argument_list|,
name|end
argument_list|,
literal|"TEST"
argument_list|)
decl_stmt|;
name|t
operator|.
name|setPositionIncrement
argument_list|(
name|posInc
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|toks
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|t
operator|=
operator|new
name|Token
argument_list|(
name|toks
index|[
name|j
index|]
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|"TEST"
argument_list|)
expr_stmt|;
name|t
operator|.
name|setPositionIncrement
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|//------------------------------------------------------------------------
comment|// These may be useful beyond test cases...
comment|//------------------------------------------------------------------------
DECL|method|getTokens
specifier|static
name|List
comment|/*<Token>*/
name|getTokens
parameter_list|(
name|TokenStream
name|tstream
parameter_list|)
throws|throws
name|IOException
block|{
name|List
comment|/*<Token>*/
name|tokens
init|=
operator|new
name|ArrayList
comment|/*<Token>*/
argument_list|()
decl_stmt|;
name|tstream
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|tstream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
specifier|final
name|Token
name|t
init|=
operator|new
name|Token
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|it
init|=
name|tstream
operator|.
name|getAttributeImplsIterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|AttributeImpl
name|att
init|=
operator|(
name|AttributeImpl
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
name|att
operator|.
name|copyTo
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|ce
parameter_list|)
block|{
comment|// ignore Attributes unsupported by Token
block|}
block|}
name|tokens
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
name|tstream
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|tokens
return|;
block|}
block|}
end_class
end_unit
