begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.ngram
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ngram
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
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|Analyzer
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
name|MockTokenizer
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
name|Tokenizer
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
name|core
operator|.
name|KeywordTokenizer
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
name|core
operator|.
name|LetterTokenizer
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
name|core
operator|.
name|WhitespaceTokenizer
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
name|OffsetAttribute
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|_TestUtil
import|;
end_import
begin_comment
comment|/**  * Tests {@link EdgeNGramTokenFilter} for correctness.  */
end_comment
begin_class
DECL|class|EdgeNGramTokenFilterTest
specifier|public
class|class
name|EdgeNGramTokenFilterTest
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|input
specifier|private
name|TokenStream
name|input
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|input
operator|=
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"abcde"
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|testInvalidInput
specifier|public
name|void
name|testInvalidInput
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|gotException
init|=
literal|false
decl_stmt|;
try|try
block|{
operator|new
name|EdgeNGramTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|input
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|gotException
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|gotException
argument_list|)
expr_stmt|;
block|}
DECL|method|testInvalidInput2
specifier|public
name|void
name|testInvalidInput2
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|gotException
init|=
literal|false
decl_stmt|;
try|try
block|{
operator|new
name|EdgeNGramTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|input
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|gotException
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|gotException
argument_list|)
expr_stmt|;
block|}
DECL|method|testInvalidInput3
specifier|public
name|void
name|testInvalidInput3
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|gotException
init|=
literal|false
decl_stmt|;
try|try
block|{
operator|new
name|EdgeNGramTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|input
argument_list|,
operator|-
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|gotException
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|gotException
argument_list|)
expr_stmt|;
block|}
DECL|method|testFrontUnigram
specifier|public
name|void
name|testFrontUnigram
parameter_list|()
throws|throws
name|Exception
block|{
name|EdgeNGramTokenFilter
name|tokenizer
init|=
operator|new
name|EdgeNGramTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|input
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|5
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testOversizedNgrams
specifier|public
name|void
name|testOversizedNgrams
parameter_list|()
throws|throws
name|Exception
block|{
name|EdgeNGramTokenFilter
name|tokenizer
init|=
operator|new
name|EdgeNGramTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|input
argument_list|,
literal|6
argument_list|,
literal|6
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|,
operator|new
name|int
index|[
literal|0
index|]
argument_list|,
operator|new
name|int
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|testFrontRangeOfNgrams
specifier|public
name|void
name|testFrontRangeOfNgrams
parameter_list|()
throws|throws
name|Exception
block|{
name|EdgeNGramTokenFilter
name|tokenizer
init|=
operator|new
name|EdgeNGramTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|input
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"ab"
block|,
literal|"abc"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|5
block|,
literal|5
block|,
literal|5
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testFilterPositions
specifier|public
name|void
name|testFilterPositions
parameter_list|()
throws|throws
name|Exception
block|{
name|TokenStream
name|ts
init|=
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"abcde vwxyz"
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|EdgeNGramTokenFilter
name|tokenizer
init|=
operator|new
name|EdgeNGramTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|ts
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"ab"
block|,
literal|"abc"
block|,
literal|"v"
block|,
literal|"vw"
block|,
literal|"vwx"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|6
block|,
literal|6
block|,
literal|6
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|5
block|,
literal|5
block|,
literal|5
block|,
literal|11
block|,
literal|11
block|,
literal|11
block|}
argument_list|,
literal|null
argument_list|,
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
literal|1
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|class|PositionFilter
specifier|private
specifier|static
class|class
name|PositionFilter
extends|extends
name|TokenFilter
block|{
DECL|field|posIncrAtt
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posIncrAtt
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|started
specifier|private
name|boolean
name|started
decl_stmt|;
DECL|method|PositionFilter
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
block|}
annotation|@
name|Override
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
name|started
condition|)
block|{
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|started
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
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|started
operator|=
literal|false
expr_stmt|;
block|}
block|}
DECL|method|testFirstTokenPositionIncrement
specifier|public
name|void
name|testFirstTokenPositionIncrement
parameter_list|()
throws|throws
name|Exception
block|{
name|TokenStream
name|ts
init|=
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"a abc"
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|ts
operator|=
operator|new
name|PositionFilter
argument_list|(
name|ts
argument_list|)
expr_stmt|;
comment|// All but first token will get 0 position increment
name|EdgeNGramTokenFilter
name|filter
init|=
operator|new
name|EdgeNGramTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|ts
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|)
decl_stmt|;
comment|// The first token "a" will not be output, since it's smaller than the mingram size of 2.
comment|// The second token on input to EdgeNGramTokenFilter will have position increment of 0,
comment|// which should be increased to 1, since this is the first output token in the stream.
name|assertTokenStreamContents
argument_list|(
name|filter
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ab"
block|,
literal|"abc"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|2
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|5
block|,
literal|5
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSmallTokenInStream
specifier|public
name|void
name|testSmallTokenInStream
parameter_list|()
throws|throws
name|Exception
block|{
name|input
operator|=
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"abc de fgh"
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|EdgeNGramTokenFilter
name|tokenizer
init|=
operator|new
name|EdgeNGramTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|input
argument_list|,
literal|3
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"abc"
block|,
literal|"fgh"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|7
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|10
block|}
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
name|WhitespaceTokenizer
name|tokenizer
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"abcde"
argument_list|)
argument_list|)
decl_stmt|;
name|EdgeNGramTokenFilter
name|filter
init|=
operator|new
name|EdgeNGramTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|tokenizer
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|filter
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"ab"
block|,
literal|"abc"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|5
block|,
literal|5
block|,
literal|5
block|}
argument_list|)
expr_stmt|;
name|tokenizer
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"abcde"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|filter
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"ab"
block|,
literal|"abc"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|5
block|,
literal|5
block|,
literal|5
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** blast some random strings through the analyzer */
DECL|method|testRandomStrings
specifier|public
name|void
name|testRandomStrings
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|EdgeNGramTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|tokenizer
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|a
argument_list|,
literal|1000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
block|}
DECL|method|testEmptyTerm
specifier|public
name|void
name|testEmptyTerm
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|KeywordTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|EdgeNGramTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|tokenizer
argument_list|,
literal|2
argument_list|,
literal|15
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|checkAnalysisConsistency
argument_list|(
name|random
argument_list|,
name|a
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
DECL|method|testGraphs
specifier|public
name|void
name|testGraphs
parameter_list|()
throws|throws
name|IOException
block|{
name|TokenStream
name|tk
init|=
operator|new
name|LetterTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"abc d efgh ij klmno p q"
argument_list|)
argument_list|)
decl_stmt|;
name|tk
operator|=
operator|new
name|ShingleFilter
argument_list|(
name|tk
argument_list|)
expr_stmt|;
name|tk
operator|=
operator|new
name|EdgeNGramTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|tk
argument_list|,
literal|7
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|tk
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tk
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"efgh ij"
block|,
literal|"ij klmn"
block|,
literal|"ij klmno"
block|,
literal|"klmno p"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|11
block|,
literal|11
block|,
literal|14
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|13
block|,
literal|19
block|,
literal|19
block|,
literal|21
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|2
block|,
literal|2
block|,
literal|2
block|}
argument_list|,
literal|23
argument_list|)
expr_stmt|;
block|}
DECL|method|testSupplementaryCharacters
specifier|public
name|void
name|testSupplementaryCharacters
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|s
init|=
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|10
argument_list|)
decl_stmt|;
specifier|final
name|int
name|codePointCount
init|=
name|s
operator|.
name|codePointCount
argument_list|(
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|minGram
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxGram
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
name|minGram
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|TokenStream
name|tk
init|=
operator|new
name|KeywordTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|s
argument_list|)
argument_list|)
decl_stmt|;
name|tk
operator|=
operator|new
name|EdgeNGramTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|tk
argument_list|,
name|minGram
argument_list|,
name|maxGram
argument_list|)
expr_stmt|;
specifier|final
name|CharTermAttribute
name|termAtt
init|=
name|tk
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|OffsetAttribute
name|offsetAtt
init|=
name|tk
operator|.
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|tk
operator|.
name|reset
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|minGram
init|;
name|i
operator|<=
name|Math
operator|.
name|min
argument_list|(
name|codePointCount
argument_list|,
name|maxGram
argument_list|)
condition|;
operator|++
name|i
control|)
block|{
name|assertTrue
argument_list|(
name|tk
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|offsetAtt
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
operator|.
name|length
argument_list|()
argument_list|,
name|offsetAtt
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|int
name|end
init|=
name|Character
operator|.
name|offsetByCodePoints
argument_list|(
name|s
argument_list|,
literal|0
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|end
argument_list|)
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|tk
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
