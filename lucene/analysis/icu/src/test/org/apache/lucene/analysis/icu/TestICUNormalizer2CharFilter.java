begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.icu
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|icu
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
name|CharFilter
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
name|ngram
operator|.
name|NGramTokenizer
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
name|TestUtil
import|;
end_import
begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|Normalizer2
import|;
end_import
begin_class
DECL|class|TestICUNormalizer2CharFilter
specifier|public
class|class
name|TestICUNormalizer2CharFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testNormalization
specifier|public
name|void
name|testNormalization
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|input
init|=
literal|"Ê°ã°ã5ââã±ãï¼ãããã¡ã¼ã®æ­£è¦åã®ãã¹ãï¼ãããããï½¶ï½·ï½¸ï½¹ï½ºï½»ï¾ï½¼ï¾ï½½ï¾ï½¾ï¾ï½¿ï¾gÌê°/ê°à®¨à®¿à¹à¸à¤·à¤¿chkÊ·à¤à¥à¤·à¤¿"
decl_stmt|;
name|Normalizer2
name|normalizer
init|=
name|Normalizer2
operator|.
name|getInstance
argument_list|(
literal|null
argument_list|,
literal|"nfkc_cf"
argument_list|,
name|Normalizer2
operator|.
name|Mode
operator|.
name|COMPOSE
argument_list|)
decl_stmt|;
name|String
name|expectedOutput
init|=
name|normalizer
operator|.
name|normalize
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|CharFilter
name|reader
init|=
operator|new
name|ICUNormalizer2CharFilter
argument_list|(
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|,
name|normalizer
argument_list|)
decl_stmt|;
name|char
index|[]
name|tempBuff
init|=
operator|new
name|char
index|[
literal|10
index|]
decl_stmt|;
name|StringBuilder
name|output
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|length
init|=
name|reader
operator|.
name|read
argument_list|(
name|tempBuff
argument_list|)
decl_stmt|;
if|if
condition|(
name|length
operator|==
operator|-
literal|1
condition|)
block|{
break|break;
block|}
name|output
operator|.
name|append
argument_list|(
name|tempBuff
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|output
operator|.
name|toString
argument_list|()
argument_list|,
name|normalizer
operator|.
name|normalize
argument_list|(
name|input
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|reader
operator|.
name|correctOffset
argument_list|(
name|output
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expectedOutput
argument_list|,
name|output
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testTokenStream
specifier|public
name|void
name|testTokenStream
parameter_list|()
throws|throws
name|IOException
block|{
comment|// 'â', 'â', 'ã±', 'ã', 'ï½»'+'<<', 'ï½¿'+'<<', 'ã°'+'<<'
name|String
name|input
init|=
literal|"â â ã± ã ï½»ï¾ ï½¿ï¾ ã°ï¾"
decl_stmt|;
name|CharFilter
name|reader
init|=
operator|new
name|ICUNormalizer2CharFilter
argument_list|(
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|,
name|Normalizer2
operator|.
name|getInstance
argument_list|(
literal|null
argument_list|,
literal|"nfkc"
argument_list|,
name|Normalizer2
operator|.
name|Mode
operator|.
name|COMPOSE
argument_list|)
argument_list|)
decl_stmt|;
name|Tokenizer
name|tokenStream
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|tokenStream
operator|.
name|setReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenStream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Â°C"
block|,
literal|"No"
block|,
literal|"(æ ª)"
block|,
literal|"ã°ã©ã "
block|,
literal|"ã¶"
block|,
literal|"ã¾"
block|,
literal|"ãã´"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|,
literal|4
block|,
literal|6
block|,
literal|8
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
literal|1
block|,
literal|3
block|,
literal|5
block|,
literal|7
block|,
literal|10
block|,
literal|13
block|,
literal|16
block|}
argument_list|,
name|input
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testTokenStream2
specifier|public
name|void
name|testTokenStream2
parameter_list|()
throws|throws
name|IOException
block|{
comment|// 'ã°', '<<'ã, '5', 'â', 'â', 'ã±', 'ã', 'ï½»', '<<', 'ï½¿', '<<'
name|String
name|input
init|=
literal|"ã°ã5ââã±ãï½»ï¾ï½¿ï¾"
decl_stmt|;
name|CharFilter
name|reader
init|=
operator|new
name|ICUNormalizer2CharFilter
argument_list|(
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|,
name|Normalizer2
operator|.
name|getInstance
argument_list|(
literal|null
argument_list|,
literal|"nfkc_cf"
argument_list|,
name|Normalizer2
operator|.
name|Mode
operator|.
name|COMPOSE
argument_list|)
argument_list|)
decl_stmt|;
name|Tokenizer
name|tokenStream
init|=
operator|new
name|NGramTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|tokenStream
operator|.
name|setReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenStream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ã"
block|,
literal|"ã´"
block|,
literal|"5"
block|,
literal|"Â°"
block|,
literal|"c"
block|,
literal|"n"
block|,
literal|"o"
block|,
literal|"("
block|,
literal|"æ ª"
block|,
literal|")"
block|,
literal|"ã°"
block|,
literal|"ã©"
block|,
literal|"ã "
block|,
literal|"ã¶"
block|,
literal|"ã¾"
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
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|6
block|,
literal|6
block|,
literal|7
block|,
literal|9
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
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|6
block|,
literal|6
block|,
literal|7
block|,
literal|9
block|,
literal|11
block|}
argument_list|,
name|input
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testMassiveLigature
specifier|public
name|void
name|testMassiveLigature
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|input
init|=
literal|"\uFDFA"
decl_stmt|;
name|CharFilter
name|reader
init|=
operator|new
name|ICUNormalizer2CharFilter
argument_list|(
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|,
name|Normalizer2
operator|.
name|getInstance
argument_list|(
literal|null
argument_list|,
literal|"nfkc_cf"
argument_list|,
name|Normalizer2
operator|.
name|Mode
operator|.
name|COMPOSE
argument_list|)
argument_list|)
decl_stmt|;
name|Tokenizer
name|tokenStream
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|tokenStream
operator|.
name|setReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenStream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ØµÙÙ"
block|,
literal|"Ø§ÙÙÙ"
block|,
literal|"Ø¹ÙÙÙ"
block|,
literal|"ÙØ³ÙÙ"
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
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|}
argument_list|,
name|input
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestMode
specifier|public
name|void
name|doTestMode
parameter_list|(
specifier|final
name|Normalizer2
name|normalizer
parameter_list|,
name|int
name|maxLength
parameter_list|,
name|int
name|iterations
parameter_list|)
throws|throws
name|IOException
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
parameter_list|)
block|{
return|return
operator|new
name|TokenStreamComponents
argument_list|(
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Reader
name|initReader
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|ICUNormalizer2CharFilter
argument_list|(
name|reader
argument_list|,
name|normalizer
argument_list|)
return|;
block|}
block|}
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
name|iterations
condition|;
name|i
operator|++
control|)
block|{
name|String
name|input
init|=
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|,
name|maxLength
argument_list|)
decl_stmt|;
if|if
condition|(
name|input
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
name|String
name|normalized
init|=
name|normalizer
operator|.
name|normalize
argument_list|(
name|input
argument_list|)
decl_stmt|;
if|if
condition|(
name|normalized
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
continue|continue;
comment|// MockTokenizer doesnt tokenize empty string...
block|}
name|checkOneTerm
argument_list|(
name|a
argument_list|,
name|input
argument_list|,
name|normalized
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testNFC
specifier|public
name|void
name|testNFC
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestMode
argument_list|(
name|Normalizer2
operator|.
name|getInstance
argument_list|(
literal|null
argument_list|,
literal|"nfc"
argument_list|,
name|Normalizer2
operator|.
name|Mode
operator|.
name|COMPOSE
argument_list|)
argument_list|,
literal|20
argument_list|,
name|RANDOM_MULTIPLIER
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
DECL|method|testNFCHuge
specifier|public
name|void
name|testNFCHuge
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestMode
argument_list|(
name|Normalizer2
operator|.
name|getInstance
argument_list|(
literal|null
argument_list|,
literal|"nfc"
argument_list|,
name|Normalizer2
operator|.
name|Mode
operator|.
name|COMPOSE
argument_list|)
argument_list|,
literal|8192
argument_list|,
name|RANDOM_MULTIPLIER
operator|*
literal|100
argument_list|)
expr_stmt|;
block|}
DECL|method|testNFD
specifier|public
name|void
name|testNFD
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestMode
argument_list|(
name|Normalizer2
operator|.
name|getInstance
argument_list|(
literal|null
argument_list|,
literal|"nfc"
argument_list|,
name|Normalizer2
operator|.
name|Mode
operator|.
name|DECOMPOSE
argument_list|)
argument_list|,
literal|20
argument_list|,
name|RANDOM_MULTIPLIER
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
DECL|method|testNFDHuge
specifier|public
name|void
name|testNFDHuge
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestMode
argument_list|(
name|Normalizer2
operator|.
name|getInstance
argument_list|(
literal|null
argument_list|,
literal|"nfc"
argument_list|,
name|Normalizer2
operator|.
name|Mode
operator|.
name|DECOMPOSE
argument_list|)
argument_list|,
literal|8192
argument_list|,
name|RANDOM_MULTIPLIER
operator|*
literal|100
argument_list|)
expr_stmt|;
block|}
DECL|method|testNFKC
specifier|public
name|void
name|testNFKC
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestMode
argument_list|(
name|Normalizer2
operator|.
name|getInstance
argument_list|(
literal|null
argument_list|,
literal|"nfkc"
argument_list|,
name|Normalizer2
operator|.
name|Mode
operator|.
name|COMPOSE
argument_list|)
argument_list|,
literal|20
argument_list|,
name|RANDOM_MULTIPLIER
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
DECL|method|testNFKCHuge
specifier|public
name|void
name|testNFKCHuge
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestMode
argument_list|(
name|Normalizer2
operator|.
name|getInstance
argument_list|(
literal|null
argument_list|,
literal|"nfkc"
argument_list|,
name|Normalizer2
operator|.
name|Mode
operator|.
name|COMPOSE
argument_list|)
argument_list|,
literal|8192
argument_list|,
name|RANDOM_MULTIPLIER
operator|*
literal|100
argument_list|)
expr_stmt|;
block|}
DECL|method|testNFKD
specifier|public
name|void
name|testNFKD
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestMode
argument_list|(
name|Normalizer2
operator|.
name|getInstance
argument_list|(
literal|null
argument_list|,
literal|"nfkc"
argument_list|,
name|Normalizer2
operator|.
name|Mode
operator|.
name|DECOMPOSE
argument_list|)
argument_list|,
literal|20
argument_list|,
name|RANDOM_MULTIPLIER
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
DECL|method|testNFKDHuge
specifier|public
name|void
name|testNFKDHuge
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestMode
argument_list|(
name|Normalizer2
operator|.
name|getInstance
argument_list|(
literal|null
argument_list|,
literal|"nfkc"
argument_list|,
name|Normalizer2
operator|.
name|Mode
operator|.
name|DECOMPOSE
argument_list|)
argument_list|,
literal|8192
argument_list|,
name|RANDOM_MULTIPLIER
operator|*
literal|100
argument_list|)
expr_stmt|;
block|}
DECL|method|testNFKC_CF
specifier|public
name|void
name|testNFKC_CF
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestMode
argument_list|(
name|Normalizer2
operator|.
name|getInstance
argument_list|(
literal|null
argument_list|,
literal|"nfkc_cf"
argument_list|,
name|Normalizer2
operator|.
name|Mode
operator|.
name|COMPOSE
argument_list|)
argument_list|,
literal|20
argument_list|,
name|RANDOM_MULTIPLIER
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
DECL|method|testNFKC_CFHuge
specifier|public
name|void
name|testNFKC_CFHuge
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestMode
argument_list|(
name|Normalizer2
operator|.
name|getInstance
argument_list|(
literal|null
argument_list|,
literal|"nfkc_cf"
argument_list|,
name|Normalizer2
operator|.
name|Mode
operator|.
name|COMPOSE
argument_list|)
argument_list|,
literal|8192
argument_list|,
name|RANDOM_MULTIPLIER
operator|*
literal|100
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandomStrings
specifier|public
name|void
name|testRandomStrings
parameter_list|()
throws|throws
name|IOException
block|{
comment|// nfkc_cf
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
parameter_list|)
block|{
return|return
operator|new
name|TokenStreamComponents
argument_list|(
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Reader
name|initReader
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|ICUNormalizer2CharFilter
argument_list|(
name|reader
argument_list|,
name|Normalizer2
operator|.
name|getInstance
argument_list|(
literal|null
argument_list|,
literal|"nfkc_cf"
argument_list|,
name|Normalizer2
operator|.
name|Mode
operator|.
name|COMPOSE
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
comment|// huge strings
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
argument_list|)
expr_stmt|;
comment|// nfkd
name|a
operator|=
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
parameter_list|)
block|{
return|return
operator|new
name|TokenStreamComponents
argument_list|(
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Reader
name|initReader
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|ICUNormalizer2CharFilter
argument_list|(
name|reader
argument_list|,
name|Normalizer2
operator|.
name|getInstance
argument_list|(
literal|null
argument_list|,
literal|"nfkc"
argument_list|,
name|Normalizer2
operator|.
name|Mode
operator|.
name|DECOMPOSE
argument_list|)
argument_list|)
return|;
block|}
block|}
expr_stmt|;
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
comment|// huge strings
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
argument_list|)
expr_stmt|;
block|}
DECL|method|testCuriousString
specifier|public
name|void
name|testCuriousString
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|text
init|=
literal|"\udb40\udc3d\uf273\ue960\u06c8\ud955\udc13\ub7fc\u0692 \u2089\u207b\u2073\u2075"
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
parameter_list|)
block|{
return|return
operator|new
name|TokenStreamComponents
argument_list|(
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Reader
name|initReader
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|ICUNormalizer2CharFilter
argument_list|(
name|reader
argument_list|,
name|Normalizer2
operator|.
name|getInstance
argument_list|(
literal|null
argument_list|,
literal|"nfkc_cf"
argument_list|,
name|Normalizer2
operator|.
name|Mode
operator|.
name|COMPOSE
argument_list|)
argument_list|)
return|;
block|}
block|}
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|checkAnalysisConsistency
argument_list|(
name|random
argument_list|()
argument_list|,
name|a
argument_list|,
literal|false
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit