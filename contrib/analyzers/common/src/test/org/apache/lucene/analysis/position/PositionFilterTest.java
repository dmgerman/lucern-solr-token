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
name|BaseTokenStreamTestCase
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
name|shingle
operator|.
name|ShingleFilter
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
DECL|class|PositionFilterTest
specifier|public
class|class
name|PositionFilterTest
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|class|TestTokenStream
specifier|public
class|class
name|TestTokenStream
extends|extends
name|TokenStream
block|{
DECL|field|index
specifier|protected
name|int
name|index
init|=
literal|0
decl_stmt|;
DECL|field|testToken
specifier|protected
name|String
index|[]
name|testToken
decl_stmt|;
DECL|field|termAtt
specifier|protected
name|TermAttribute
name|termAtt
decl_stmt|;
DECL|method|TestTokenStream
specifier|public
name|TestTokenStream
parameter_list|(
name|String
index|[]
name|testToken
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|testToken
operator|=
name|testToken
expr_stmt|;
name|termAtt
operator|=
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
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
name|clearAttributes
argument_list|()
expr_stmt|;
if|if
condition|(
name|index
operator|<
name|testToken
operator|.
name|length
condition|)
block|{
name|termAtt
operator|.
name|setTermBuffer
argument_list|(
name|testToken
index|[
name|index
operator|++
index|]
argument_list|)
expr_stmt|;
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
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|index
operator|=
literal|0
expr_stmt|;
block|}
block|}
DECL|field|TEST_TOKEN
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|TEST_TOKEN
init|=
operator|new
name|String
index|[]
block|{
literal|"please"
block|,
literal|"divide"
block|,
literal|"this"
block|,
literal|"sentence"
block|,
literal|"into"
block|,
literal|"shingles"
block|,   }
decl_stmt|;
DECL|field|TEST_TOKEN_POSITION_INCREMENTS
specifier|public
specifier|static
specifier|final
name|int
index|[]
name|TEST_TOKEN_POSITION_INCREMENTS
init|=
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
decl_stmt|;
DECL|field|TEST_TOKEN_NON_ZERO_POSITION_INCREMENTS
specifier|public
specifier|static
specifier|final
name|int
index|[]
name|TEST_TOKEN_NON_ZERO_POSITION_INCREMENTS
init|=
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|5
block|,
literal|5
block|,
literal|5
block|,
literal|5
block|,
literal|5
block|}
decl_stmt|;
DECL|field|SIX_GRAM_NO_POSITIONS_TOKENS
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|SIX_GRAM_NO_POSITIONS_TOKENS
init|=
operator|new
name|String
index|[]
block|{
literal|"please"
block|,
literal|"please divide"
block|,
literal|"please divide this"
block|,
literal|"please divide this sentence"
block|,
literal|"please divide this sentence into"
block|,
literal|"please divide this sentence into shingles"
block|,
literal|"divide"
block|,
literal|"divide this"
block|,
literal|"divide this sentence"
block|,
literal|"divide this sentence into"
block|,
literal|"divide this sentence into shingles"
block|,
literal|"this"
block|,
literal|"this sentence"
block|,
literal|"this sentence into"
block|,
literal|"this sentence into shingles"
block|,
literal|"sentence"
block|,
literal|"sentence into"
block|,
literal|"sentence into shingles"
block|,
literal|"into"
block|,
literal|"into shingles"
block|,
literal|"shingles"
block|,   }
decl_stmt|;
DECL|field|SIX_GRAM_NO_POSITIONS_INCREMENTS
specifier|public
specifier|static
specifier|final
name|int
index|[]
name|SIX_GRAM_NO_POSITIONS_INCREMENTS
init|=
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
decl_stmt|;
DECL|field|SIX_GRAM_NO_POSITIONS_TYPES
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|SIX_GRAM_NO_POSITIONS_TYPES
init|=
operator|new
name|String
index|[]
block|{
literal|"word"
block|,
literal|"shingle"
block|,
literal|"shingle"
block|,
literal|"shingle"
block|,
literal|"shingle"
block|,
literal|"shingle"
block|,
literal|"word"
block|,
literal|"shingle"
block|,
literal|"shingle"
block|,
literal|"shingle"
block|,
literal|"shingle"
block|,
literal|"word"
block|,
literal|"shingle"
block|,
literal|"shingle"
block|,
literal|"shingle"
block|,
literal|"word"
block|,
literal|"shingle"
block|,
literal|"shingle"
block|,
literal|"word"
block|,
literal|"shingle"
block|,
literal|"word"
block|}
decl_stmt|;
DECL|method|testFilter
specifier|public
name|void
name|testFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTokenStreamContents
argument_list|(
operator|new
name|PositionFilter
argument_list|(
operator|new
name|TestTokenStream
argument_list|(
name|TEST_TOKEN
argument_list|)
argument_list|)
argument_list|,
name|TEST_TOKEN
argument_list|,
name|TEST_TOKEN_POSITION_INCREMENTS
argument_list|)
expr_stmt|;
block|}
DECL|method|testNonZeroPositionIncrement
specifier|public
name|void
name|testNonZeroPositionIncrement
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTokenStreamContents
argument_list|(
operator|new
name|PositionFilter
argument_list|(
operator|new
name|TestTokenStream
argument_list|(
name|TEST_TOKEN
argument_list|)
argument_list|,
literal|5
argument_list|)
argument_list|,
name|TEST_TOKEN
argument_list|,
name|TEST_TOKEN_NON_ZERO_POSITION_INCREMENTS
argument_list|)
expr_stmt|;
block|}
DECL|method|testReset
specifier|public
name|void
name|testReset
parameter_list|()
throws|throws
name|Exception
block|{
name|PositionFilter
name|filter
init|=
operator|new
name|PositionFilter
argument_list|(
operator|new
name|TestTokenStream
argument_list|(
name|TEST_TOKEN
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|filter
argument_list|,
name|TEST_TOKEN
argument_list|,
name|TEST_TOKEN_POSITION_INCREMENTS
argument_list|)
expr_stmt|;
name|filter
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// Make sure that the reset filter provides correct position increments
name|assertTokenStreamContents
argument_list|(
name|filter
argument_list|,
name|TEST_TOKEN
argument_list|,
name|TEST_TOKEN_POSITION_INCREMENTS
argument_list|)
expr_stmt|;
block|}
comment|/** Tests ShingleFilter up to six shingles against six terms.    *  Tests PositionFilter setting all but the first positionIncrement to zero.    * @throws java.io.IOException @see Token#next(Token)    */
DECL|method|test6GramFilterNoPositions
specifier|public
name|void
name|test6GramFilterNoPositions
parameter_list|()
throws|throws
name|Exception
block|{
name|ShingleFilter
name|filter
init|=
operator|new
name|ShingleFilter
argument_list|(
operator|new
name|TestTokenStream
argument_list|(
name|TEST_TOKEN
argument_list|)
argument_list|,
literal|6
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
operator|new
name|PositionFilter
argument_list|(
name|filter
argument_list|)
argument_list|,
name|SIX_GRAM_NO_POSITIONS_TOKENS
argument_list|,
name|SIX_GRAM_NO_POSITIONS_INCREMENTS
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
