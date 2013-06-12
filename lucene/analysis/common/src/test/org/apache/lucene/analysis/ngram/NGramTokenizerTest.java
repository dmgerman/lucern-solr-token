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
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ngram
operator|.
name|NGramTokenizerTest
operator|.
name|isTokenChar
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
name|Arrays
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PositionLengthAttribute
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
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomStrings
import|;
end_import
begin_comment
comment|/**  * Tests {@link NGramTokenizer} for correctness.  */
end_comment
begin_class
DECL|class|NGramTokenizerTest
specifier|public
class|class
name|NGramTokenizerTest
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|input
specifier|private
name|StringReader
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
name|StringReader
argument_list|(
literal|"abcde"
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
name|NGramTokenizer
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
name|NGramTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|input
argument_list|,
literal|0
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
DECL|method|testUnigrams
specifier|public
name|void
name|testUnigrams
parameter_list|()
throws|throws
name|Exception
block|{
name|NGramTokenizer
name|tokenizer
init|=
operator|new
name|NGramTokenizer
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
block|,
literal|"b"
block|,
literal|"c"
block|,
literal|"d"
block|,
literal|"e"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|}
argument_list|,
literal|5
comment|/* abcde */
argument_list|)
expr_stmt|;
block|}
DECL|method|testBigrams
specifier|public
name|void
name|testBigrams
parameter_list|()
throws|throws
name|Exception
block|{
name|NGramTokenizer
name|tokenizer
init|=
operator|new
name|NGramTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|input
argument_list|,
literal|2
argument_list|,
literal|2
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
literal|"ab"
block|,
literal|"bc"
block|,
literal|"cd"
block|,
literal|"de"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|}
argument_list|,
literal|5
comment|/* abcde */
argument_list|)
expr_stmt|;
block|}
DECL|method|testNgrams
specifier|public
name|void
name|testNgrams
parameter_list|()
throws|throws
name|Exception
block|{
name|NGramTokenizer
name|tokenizer
init|=
operator|new
name|NGramTokenizer
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
block|,
literal|"b"
block|,
literal|"bc"
block|,
literal|"bcd"
block|,
literal|"c"
block|,
literal|"cd"
block|,
literal|"cde"
block|,
literal|"d"
block|,
literal|"de"
block|,
literal|"e"
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
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|2
block|,
literal|2
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|}
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|5
comment|/* abcde */
argument_list|,
literal|false
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
name|NGramTokenizer
name|tokenizer
init|=
operator|new
name|NGramTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|input
argument_list|,
literal|6
argument_list|,
literal|7
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
argument_list|,
literal|5
comment|/* abcde */
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
name|NGramTokenizer
name|tokenizer
init|=
operator|new
name|NGramTokenizer
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
block|,
literal|"b"
block|,
literal|"c"
block|,
literal|"d"
block|,
literal|"e"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|}
argument_list|,
literal|5
comment|/* abcde */
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
name|tokenizer
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|,
literal|"c"
block|,
literal|"d"
block|,
literal|"e"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|}
argument_list|,
literal|5
comment|/* abcde */
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
name|NGramTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|reader
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|tokenizer
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
argument_list|,
literal|20
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|a
argument_list|,
literal|50
operator|*
name|RANDOM_MULTIPLIER
argument_list|,
literal|1027
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|testNGrams
specifier|private
specifier|static
name|void
name|testNGrams
parameter_list|(
name|int
name|minGram
parameter_list|,
name|int
name|maxGram
parameter_list|,
name|int
name|length
parameter_list|,
specifier|final
name|String
name|nonTokenChars
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|s
init|=
name|RandomStrings
operator|.
name|randomAsciiOfLength
argument_list|(
name|random
argument_list|()
argument_list|,
name|length
argument_list|)
decl_stmt|;
name|testNGrams
argument_list|(
name|minGram
argument_list|,
name|maxGram
argument_list|,
name|s
argument_list|,
name|nonTokenChars
argument_list|)
expr_stmt|;
block|}
DECL|method|testNGrams
specifier|private
specifier|static
name|void
name|testNGrams
parameter_list|(
name|int
name|minGram
parameter_list|,
name|int
name|maxGram
parameter_list|,
name|String
name|s
parameter_list|,
name|String
name|nonTokenChars
parameter_list|)
throws|throws
name|IOException
block|{
name|testNGrams
argument_list|(
name|minGram
argument_list|,
name|maxGram
argument_list|,
name|s
argument_list|,
name|nonTokenChars
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|toCodePoints
specifier|static
name|int
index|[]
name|toCodePoints
parameter_list|(
name|CharSequence
name|s
parameter_list|)
block|{
specifier|final
name|int
index|[]
name|codePoints
init|=
operator|new
name|int
index|[
name|Character
operator|.
name|codePointCount
argument_list|(
name|s
argument_list|,
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|)
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|j
init|=
literal|0
init|;
name|i
operator|<
name|s
operator|.
name|length
argument_list|()
condition|;
operator|++
name|j
control|)
block|{
name|codePoints
index|[
name|j
index|]
operator|=
name|Character
operator|.
name|codePointAt
argument_list|(
name|s
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|i
operator|+=
name|Character
operator|.
name|charCount
argument_list|(
name|codePoints
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|codePoints
return|;
block|}
DECL|method|isTokenChar
specifier|static
name|boolean
name|isTokenChar
parameter_list|(
name|String
name|nonTokenChars
parameter_list|,
name|int
name|codePoint
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nonTokenChars
operator|.
name|length
argument_list|()
condition|;
control|)
block|{
specifier|final
name|int
name|cp
init|=
name|nonTokenChars
operator|.
name|codePointAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|cp
operator|==
name|codePoint
condition|)
block|{
return|return
literal|false
return|;
block|}
name|i
operator|+=
name|Character
operator|.
name|charCount
argument_list|(
name|cp
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|testNGrams
specifier|static
name|void
name|testNGrams
parameter_list|(
name|int
name|minGram
parameter_list|,
name|int
name|maxGram
parameter_list|,
name|String
name|s
parameter_list|,
specifier|final
name|String
name|nonTokenChars
parameter_list|,
name|boolean
name|edgesOnly
parameter_list|)
throws|throws
name|IOException
block|{
comment|// convert the string to code points
specifier|final
name|int
index|[]
name|codePoints
init|=
name|toCodePoints
argument_list|(
name|s
argument_list|)
decl_stmt|;
specifier|final
name|int
index|[]
name|offsets
init|=
operator|new
name|int
index|[
name|codePoints
operator|.
name|length
operator|+
literal|1
index|]
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
name|codePoints
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|offsets
index|[
name|i
operator|+
literal|1
index|]
operator|=
name|offsets
index|[
name|i
index|]
operator|+
name|Character
operator|.
name|charCount
argument_list|(
name|codePoints
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
specifier|final
name|TokenStream
name|grams
init|=
operator|new
name|NGramTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
name|s
argument_list|)
argument_list|,
name|minGram
argument_list|,
name|maxGram
argument_list|,
name|edgesOnly
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|isTokenChar
parameter_list|(
name|int
name|chr
parameter_list|)
block|{
return|return
name|nonTokenChars
operator|.
name|indexOf
argument_list|(
name|chr
argument_list|)
operator|<
literal|0
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|CharTermAttribute
name|termAtt
init|=
name|grams
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|PositionIncrementAttribute
name|posIncAtt
init|=
name|grams
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|PositionLengthAttribute
name|posLenAtt
init|=
name|grams
operator|.
name|addAttribute
argument_list|(
name|PositionLengthAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|OffsetAttribute
name|offsetAtt
init|=
name|grams
operator|.
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|grams
operator|.
name|reset
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|start
init|=
literal|0
init|;
name|start
operator|<
name|codePoints
operator|.
name|length
condition|;
operator|++
name|start
control|)
block|{
name|nextGram
label|:
for|for
control|(
name|int
name|end
init|=
name|start
operator|+
name|minGram
init|;
name|end
operator|<=
name|start
operator|+
name|maxGram
operator|&&
name|end
operator|<=
name|codePoints
operator|.
name|length
condition|;
operator|++
name|end
control|)
block|{
if|if
condition|(
name|edgesOnly
operator|&&
name|start
operator|>
literal|0
operator|&&
name|isTokenChar
argument_list|(
name|nonTokenChars
argument_list|,
name|codePoints
index|[
name|start
operator|-
literal|1
index|]
argument_list|)
condition|)
block|{
comment|// not on an edge
continue|continue
name|nextGram
continue|;
block|}
for|for
control|(
name|int
name|j
init|=
name|start
init|;
name|j
operator|<
name|end
condition|;
operator|++
name|j
control|)
block|{
if|if
condition|(
operator|!
name|isTokenChar
argument_list|(
name|nonTokenChars
argument_list|,
name|codePoints
index|[
name|j
index|]
argument_list|)
condition|)
block|{
continue|continue
name|nextGram
continue|;
block|}
block|}
name|assertTrue
argument_list|(
name|grams
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|codePoints
argument_list|,
name|start
argument_list|,
name|end
argument_list|)
argument_list|,
name|toCodePoints
argument_list|(
name|termAtt
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|posIncAtt
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|posLenAtt
operator|.
name|getPositionLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|offsets
index|[
name|start
index|]
argument_list|,
name|offsetAtt
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|offsets
index|[
name|end
index|]
argument_list|,
name|offsetAtt
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertFalse
argument_list|(
name|grams
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|grams
operator|.
name|end
argument_list|()
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
block|}
DECL|method|testLargeInput
specifier|public
name|void
name|testLargeInput
parameter_list|()
throws|throws
name|IOException
block|{
comment|// test sliding
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
literal|100
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
literal|100
argument_list|)
decl_stmt|;
name|testNGrams
argument_list|(
name|minGram
argument_list|,
name|maxGram
argument_list|,
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|3
operator|*
literal|1024
argument_list|,
literal|4
operator|*
literal|1024
argument_list|)
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
DECL|method|testLargeMaxGram
specifier|public
name|void
name|testLargeMaxGram
parameter_list|()
throws|throws
name|IOException
block|{
comment|// test sliding with maxGram> 1024
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
literal|1290
argument_list|,
literal|1300
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
literal|1300
argument_list|)
decl_stmt|;
name|testNGrams
argument_list|(
name|minGram
argument_list|,
name|maxGram
argument_list|,
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|3
operator|*
literal|1024
argument_list|,
literal|4
operator|*
literal|1024
argument_list|)
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
DECL|method|testPreTokenization
specifier|public
name|void
name|testPreTokenization
parameter_list|()
throws|throws
name|IOException
block|{
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
literal|100
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
literal|100
argument_list|)
decl_stmt|;
name|testNGrams
argument_list|(
name|minGram
argument_list|,
name|maxGram
argument_list|,
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|4
operator|*
literal|1024
argument_list|)
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
block|}
DECL|method|testHeavyPreTokenization
specifier|public
name|void
name|testHeavyPreTokenization
parameter_list|()
throws|throws
name|IOException
block|{
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
literal|100
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
literal|100
argument_list|)
decl_stmt|;
name|testNGrams
argument_list|(
name|minGram
argument_list|,
name|maxGram
argument_list|,
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|4
operator|*
literal|1024
argument_list|)
argument_list|,
literal|"abcdef"
argument_list|)
expr_stmt|;
block|}
DECL|method|testFewTokenChars
specifier|public
name|void
name|testFewTokenChars
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|char
index|[]
name|chrs
init|=
operator|new
name|char
index|[
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|4000
argument_list|,
literal|5000
argument_list|)
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|chrs
argument_list|,
literal|' '
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|chrs
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextFloat
argument_list|()
operator|<
literal|0.1
condition|)
block|{
name|chrs
index|[
name|i
index|]
operator|=
literal|'a'
expr_stmt|;
block|}
block|}
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
literal|2
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
literal|2
argument_list|)
decl_stmt|;
name|testNGrams
argument_list|(
name|minGram
argument_list|,
name|maxGram
argument_list|,
operator|new
name|String
argument_list|(
name|chrs
argument_list|)
argument_list|,
literal|" "
argument_list|)
expr_stmt|;
block|}
DECL|method|testFullUTF8Range
specifier|public
name|void
name|testFullUTF8Range
parameter_list|()
throws|throws
name|IOException
block|{
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
literal|100
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
literal|100
argument_list|)
decl_stmt|;
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
literal|4
operator|*
literal|1024
argument_list|)
decl_stmt|;
name|testNGrams
argument_list|(
name|minGram
argument_list|,
name|maxGram
argument_list|,
name|s
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|testNGrams
argument_list|(
name|minGram
argument_list|,
name|maxGram
argument_list|,
name|s
argument_list|,
literal|"abcdef"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
