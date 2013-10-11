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
name|miscellaneous
operator|.
name|ASCIIFoldingFilter
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
name|util
operator|.
name|Version
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
begin_comment
comment|/**  * Tests {@link NGramTokenFilter} for correctness.  */
end_comment
begin_class
DECL|class|NGramTokenFilterTest
specifier|public
class|class
name|NGramTokenFilterTest
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
name|NGramTokenFilter
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
name|NGramTokenFilter
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
name|NGramTokenFilter
name|filter
init|=
operator|new
name|NGramTokenFilter
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
name|filter
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
literal|0
block|,
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
block|,
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
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
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
name|NGramTokenFilter
name|filter
init|=
operator|new
name|NGramTokenFilter
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
name|filter
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
block|,
literal|0
block|,
literal|0
block|}
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
name|NGramTokenFilter
name|filter
init|=
operator|new
name|NGramTokenFilter
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
literal|5
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
block|,
literal|5
block|,
literal|5
block|,
literal|5
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
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|testNgramsNoIncrement
specifier|public
name|void
name|testNgramsNoIncrement
parameter_list|()
throws|throws
name|Exception
block|{
name|NGramTokenFilter
name|filter
init|=
operator|new
name|NGramTokenFilter
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
literal|5
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
block|,
literal|5
block|,
literal|5
block|,
literal|5
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
argument_list|,
literal|null
argument_list|,
literal|null
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
name|NGramTokenFilter
name|filter
init|=
operator|new
name|NGramTokenFilter
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
name|filter
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
name|NGramTokenFilter
name|filter
init|=
operator|new
name|NGramTokenFilter
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
name|filter
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
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
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
name|NGramTokenFilter
name|filter
init|=
operator|new
name|NGramTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|tokenizer
argument_list|,
literal|1
argument_list|,
literal|1
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
literal|0
block|,
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
block|,
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
block|,
literal|0
block|,
literal|0
block|,
literal|0
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
literal|0
block|,
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
block|,
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
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|)
expr_stmt|;
block|}
comment|// LUCENE-3642
comment|// EdgeNgram blindly adds term length to offset, but this can take things out of bounds
comment|// wrt original text if a previous filter increases the length of the word (in this case Ã¦ -> ae)
comment|// so in this case we behave like WDF, and preserve any modified offsets
DECL|method|testInvalidOffsets
specifier|public
name|void
name|testInvalidOffsets
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|analyzer
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
name|TokenFilter
name|filters
init|=
operator|new
name|ASCIIFoldingFilter
argument_list|(
name|tokenizer
argument_list|)
decl_stmt|;
name|filters
operator|=
operator|new
name|NGramTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|filters
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|filters
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"mosfellsbÃ¦r"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mo"
block|,
literal|"os"
block|,
literal|"sf"
block|,
literal|"fe"
block|,
literal|"el"
block|,
literal|"ll"
block|,
literal|"ls"
block|,
literal|"sb"
block|,
literal|"ba"
block|,
literal|"ae"
block|,
literal|"er"
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
argument_list|,
operator|new
name|int
index|[]
block|{
literal|11
block|,
literal|11
block|,
literal|11
block|,
literal|11
block|,
literal|11
block|,
literal|11
block|,
literal|11
block|,
literal|11
block|,
literal|11
block|,
literal|11
block|,
literal|11
block|}
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|min
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|,
literal|10
argument_list|)
decl_stmt|;
specifier|final
name|int
name|max
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
name|min
argument_list|,
literal|20
argument_list|)
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
name|NGramTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|tokenizer
argument_list|,
name|min
argument_list|,
name|max
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
literal|200
operator|*
name|RANDOM_MULTIPLIER
argument_list|,
literal|20
argument_list|)
expr_stmt|;
block|}
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
name|NGramTokenFilter
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
DECL|method|testLucene43
specifier|public
name|void
name|testLucene43
parameter_list|()
throws|throws
name|IOException
block|{
name|NGramTokenFilter
name|filter
init|=
operator|new
name|NGramTokenFilter
argument_list|(
name|Version
operator|.
name|LUCENE_43
argument_list|,
name|input
argument_list|,
literal|2
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
literal|"ab"
block|,
literal|"bc"
block|,
literal|"cd"
block|,
literal|"de"
block|,
literal|"abc"
block|,
literal|"bcd"
block|,
literal|"cde"
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
literal|0
block|,
literal|1
block|,
literal|2
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
block|,
literal|3
block|,
literal|4
block|,
literal|5
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
block|,
literal|1
block|,
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
name|NGramTokenFilter
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
name|start
init|=
literal|0
init|;
name|start
operator|<
name|codePointCount
condition|;
operator|++
name|start
control|)
block|{
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
name|Math
operator|.
name|min
argument_list|(
name|codePointCount
argument_list|,
name|start
operator|+
name|maxGram
argument_list|)
condition|;
operator|++
name|end
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
name|startIndex
init|=
name|Character
operator|.
name|offsetByCodePoints
argument_list|(
name|s
argument_list|,
literal|0
argument_list|,
name|start
argument_list|)
decl_stmt|;
specifier|final
name|int
name|endIndex
init|=
name|Character
operator|.
name|offsetByCodePoints
argument_list|(
name|s
argument_list|,
literal|0
argument_list|,
name|end
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s
operator|.
name|substring
argument_list|(
name|startIndex
argument_list|,
name|endIndex
argument_list|)
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
