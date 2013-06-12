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
comment|/**  * Tests {@link EdgeNGramTokenizer} for correctness.  */
end_comment
begin_class
DECL|class|EdgeNGramTokenizerTest
specifier|public
class|class
name|EdgeNGramTokenizerTest
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
name|EdgeNGramTokenizer
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
name|EdgeNGramTokenizer
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
name|EdgeNGramTokenizer
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
name|EdgeNGramTokenizer
name|tokenizer
init|=
operator|new
name|EdgeNGramTokenizer
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
literal|1
block|}
argument_list|,
literal|5
comment|/* abcde */
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
name|EdgeNGramTokenizer
name|tokenizer
init|=
operator|new
name|EdgeNGramTokenizer
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
argument_list|,
literal|5
comment|/* abcde */
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
name|EdgeNGramTokenizer
name|tokenizer
init|=
operator|new
name|EdgeNGramTokenizer
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
literal|1
block|,
literal|2
block|,
literal|3
block|}
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
name|EdgeNGramTokenizer
name|tokenizer
init|=
operator|new
name|EdgeNGramTokenizer
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
literal|1
block|,
literal|2
block|,
literal|3
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
literal|1
block|,
literal|2
block|,
literal|3
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
name|EdgeNGramTokenizer
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
literal|100
operator|*
name|RANDOM_MULTIPLIER
argument_list|,
literal|8192
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|testTokenizerPositions
specifier|public
name|void
name|testTokenizerPositions
parameter_list|()
throws|throws
name|Exception
block|{
name|EdgeNGramTokenizer
name|tokenizer
init|=
operator|new
name|EdgeNGramTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"abcde"
argument_list|)
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
literal|1
block|,
literal|2
block|,
literal|3
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
literal|1
block|,
literal|1
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
name|NGramTokenizerTest
operator|.
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
literal|true
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
