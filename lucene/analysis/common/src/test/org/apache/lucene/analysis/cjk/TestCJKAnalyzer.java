begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.cjk
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cjk
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
name|CharReader
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
name|charfilter
operator|.
name|MappingCharFilter
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
name|charfilter
operator|.
name|NormalizeCharMap
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
name|StopFilter
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
name|standard
operator|.
name|StandardTokenizer
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
name|TypeAttribute
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
name|util
operator|.
name|CharArraySet
import|;
end_import
begin_comment
comment|/**  * Most tests adopted from TestCJKTokenizer  */
end_comment
begin_class
DECL|class|TestCJKAnalyzer
specifier|public
class|class
name|TestCJKAnalyzer
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
init|=
operator|new
name|CJKAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
DECL|method|testJa1
specifier|public
name|void
name|testJa1
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ä¸äºä¸åäºå­ä¸å«ä¹å"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ä¸äº"
block|,
literal|"äºä¸"
block|,
literal|"ä¸å"
block|,
literal|"åäº"
block|,
literal|"äºå­"
block|,
literal|"å­ä¸"
block|,
literal|"ä¸å«"
block|,
literal|"å«ä¹"
block|,
literal|"ä¹å"
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
block|,
literal|5
block|,
literal|6
block|,
literal|7
block|,
literal|8
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
literal|6
block|,
literal|7
block|,
literal|8
block|,
literal|9
block|,
literal|10
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|}
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
block|,
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testJa2
specifier|public
name|void
name|testJa2
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ä¸ äºä¸å äºå­ä¸å«ä¹ å"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ä¸"
block|,
literal|"äºä¸"
block|,
literal|"ä¸å"
block|,
literal|"äºå­"
block|,
literal|"å­ä¸"
block|,
literal|"ä¸å«"
block|,
literal|"å«ä¹"
block|,
literal|"å"
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
literal|3
block|,
literal|6
block|,
literal|7
block|,
literal|8
block|,
literal|9
block|,
literal|12
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|4
block|,
literal|5
block|,
literal|8
block|,
literal|9
block|,
literal|10
block|,
literal|11
block|,
literal|13
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<SINGLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<SINGLE>"
block|}
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
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testC
specifier|public
name|void
name|testC
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"abc defgh ijklmn opqrstu vwxy z"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"abc"
block|,
literal|"defgh"
block|,
literal|"ijklmn"
block|,
literal|"opqrstu"
block|,
literal|"vwxy"
block|,
literal|"z"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|4
block|,
literal|10
block|,
literal|17
block|,
literal|25
block|,
literal|30
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|9
block|,
literal|16
block|,
literal|24
block|,
literal|29
block|,
literal|31
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|}
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
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * LUCENE-2207: wrong offset calculated by end()     */
DECL|method|testFinalOffset
specifier|public
name|void
name|testFinalOffset
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ãã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ãã"
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
literal|2
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<DOUBLE>"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ãã   "
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ãã"
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
literal|2
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<DOUBLE>"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"test"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"test"
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
literal|4
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<ALPHANUM>"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"test   "
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"test"
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
literal|4
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<ALPHANUM>"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ããtest"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ãã"
block|,
literal|"test"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
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
literal|6
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<DOUBLE>"
block|,
literal|"<ALPHANUM>"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"testãã    "
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"test"
block|,
literal|"ãã"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|4
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|4
block|,
literal|6
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<ALPHANUM>"
block|,
literal|"<DOUBLE>"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testMix
specifier|public
name|void
name|testMix
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ãããããabcããããã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"abc"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ãã"
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
literal|5
block|,
literal|8
block|,
literal|9
block|,
literal|10
block|,
literal|11
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
literal|8
block|,
literal|10
block|,
literal|11
block|,
literal|12
block|,
literal|13
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|}
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
block|,
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testMix2
specifier|public
name|void
name|testMix2
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ãããããabãcãããã ã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ab"
block|,
literal|"ã"
block|,
literal|"c"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ã"
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
literal|5
block|,
literal|7
block|,
literal|8
block|,
literal|9
block|,
literal|10
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
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|,
literal|7
block|,
literal|8
block|,
literal|9
block|,
literal|11
block|,
literal|12
block|,
literal|13
block|,
literal|15
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<SINGLE>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<SINGLE>"
block|}
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
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Non-english text (outside of CJK) is treated normally, according to unicode rules     */
DECL|method|testNonIdeographic
specifier|public
name|void
name|testNonIdeographic
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ä¸ Ø±ÙØ¨Ø±Øª ÙÙÙØ±"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ä¸"
block|,
literal|"Ø±ÙØ¨Ø±Øª"
block|,
literal|"ÙÙÙØ±"
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
literal|8
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|7
block|,
literal|12
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<SINGLE>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|}
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
argument_list|)
expr_stmt|;
block|}
comment|/**    * Same as the above, except with a nonspacing mark to show correctness.    */
DECL|method|testNonIdeographicNonLetter
specifier|public
name|void
name|testNonIdeographicNonLetter
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ä¸ Ø±ÙÙØ¨Ø±Øª ÙÙÙØ±"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ä¸"
block|,
literal|"Ø±ÙÙØ¨Ø±Øª"
block|,
literal|"ÙÙÙØ±"
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
literal|9
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|8
block|,
literal|13
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<SINGLE>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|}
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
argument_list|)
expr_stmt|;
block|}
DECL|method|testSurrogates
specifier|public
name|void
name|testSurrogates
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ð©¬è±éä¹æ¯ç"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ð©¬è±"
block|,
literal|"è±é"
block|,
literal|"éä¹"
block|,
literal|"ä¹æ¯"
block|,
literal|"æ¯ç"
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
literal|3
block|,
literal|4
block|,
literal|5
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|4
block|,
literal|5
block|,
literal|6
block|,
literal|7
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|}
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
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testReusableTokenStream
specifier|public
name|void
name|testReusableTokenStream
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesToReuse
argument_list|(
name|analyzer
argument_list|,
literal|"ãããããabcããããã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"abc"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ãã"
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
literal|5
block|,
literal|8
block|,
literal|9
block|,
literal|10
block|,
literal|11
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
literal|8
block|,
literal|10
block|,
literal|11
block|,
literal|12
block|,
literal|13
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|}
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
block|,
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|analyzer
argument_list|,
literal|"ãããããabãcãããã ã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ab"
block|,
literal|"ã"
block|,
literal|"c"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ã"
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
literal|5
block|,
literal|7
block|,
literal|8
block|,
literal|9
block|,
literal|10
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
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|,
literal|7
block|,
literal|8
block|,
literal|9
block|,
literal|11
block|,
literal|12
block|,
literal|13
block|,
literal|15
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<SINGLE>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<SINGLE>"
block|}
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
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSingleChar
specifier|public
name|void
name|testSingleChar
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ä¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ä¸"
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
operator|new
name|String
index|[]
block|{
literal|"<SINGLE>"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|}
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
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ä¸ä¸ä¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ä¸ä¸"
block|,
literal|"ä¸ä¸"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
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
literal|3
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** test that offsets are correct when mappingcharfilter is previously applied */
DECL|method|testChangedOffsets
specifier|public
name|void
name|testChangedOffsets
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|NormalizeCharMap
operator|.
name|Builder
name|builder
init|=
operator|new
name|NormalizeCharMap
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
literal|"a"
argument_list|,
literal|"ä¸äº"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
literal|"b"
argument_list|,
literal|"äºä¸"
argument_list|)
expr_stmt|;
specifier|final
name|NormalizeCharMap
name|norm
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
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
name|StandardTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
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
name|CJKBigramFilter
argument_list|(
name|tokenizer
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
name|MappingCharFilter
argument_list|(
name|norm
argument_list|,
name|CharReader
operator|.
name|get
argument_list|(
name|reader
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ab"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ä¸äº"
block|,
literal|"äºäº"
block|,
literal|"äºä¸"
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
literal|1
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|2
block|}
argument_list|)
expr_stmt|;
comment|// note: offsets are strange since this is how the charfilter maps them...
comment|// before bigramming, the 4 tokens look like:
comment|//   { 0, 0, 1, 1 },
comment|//   { 0, 1, 1, 2 }
block|}
DECL|class|FakeStandardTokenizer
specifier|private
specifier|static
class|class
name|FakeStandardTokenizer
extends|extends
name|TokenFilter
block|{
DECL|field|typeAtt
specifier|final
name|TypeAttribute
name|typeAtt
init|=
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|FakeStandardTokenizer
specifier|public
name|FakeStandardTokenizer
parameter_list|(
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
name|typeAtt
operator|.
name|setType
argument_list|(
name|StandardTokenizer
operator|.
name|TOKEN_TYPES
index|[
name|StandardTokenizer
operator|.
name|IDEOGRAPHIC
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
block|}
DECL|method|testSingleChar2
specifier|public
name|void
name|testSingleChar2
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
name|filter
init|=
operator|new
name|FakeStandardTokenizer
argument_list|(
name|tokenizer
argument_list|)
decl_stmt|;
name|filter
operator|=
operator|new
name|StopFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|filter
argument_list|,
name|CharArraySet
operator|.
name|EMPTY_SET
argument_list|)
expr_stmt|;
name|filter
operator|=
operator|new
name|CJKBigramFilter
argument_list|(
name|filter
argument_list|)
expr_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|filter
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ä¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ä¸"
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
operator|new
name|String
index|[]
block|{
literal|"<SINGLE>"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
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
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
operator|new
name|CJKAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|,
literal|10000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
block|}
comment|/** blast some random strings through the analyzer */
DECL|method|testRandomHugeStrings
specifier|public
name|void
name|testRandomHugeStrings
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
name|checkRandomData
argument_list|(
name|random
argument_list|,
operator|new
name|CJKAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|,
literal|200
operator|*
name|RANDOM_MULTIPLIER
argument_list|,
literal|8192
argument_list|)
expr_stmt|;
block|}
DECL|method|testEmptyTerm
specifier|public
name|void
name|testEmptyTerm
parameter_list|()
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
name|CJKBigramFilter
argument_list|(
name|tokenizer
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
