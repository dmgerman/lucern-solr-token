begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.th
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|th
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
name|StopAnalyzer
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
name|FlagsAttribute
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
comment|/**  * Test case for ThaiAnalyzer, modified from TestFrenchAnalyzer  *  */
end_comment
begin_class
DECL|class|TestThaiAnalyzer
specifier|public
class|class
name|TestThaiAnalyzer
extends|extends
name|BaseTokenStreamTestCase
block|{
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
name|assumeTrue
argument_list|(
literal|"JRE does not support Thai dictionary-based BreakIterator"
argument_list|,
name|ThaiTokenizer
operator|.
name|DBBI_AVAILABLE
argument_list|)
expr_stmt|;
block|}
comment|/*     * testcase for offsets    */
DECL|method|testOffsets
specifier|public
name|void
name|testOffsets
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
operator|new
name|ThaiAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|CharArraySet
operator|.
name|EMPTY_SET
argument_list|)
argument_list|,
literal|"à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à¸à¸²à¸£"
block|,
literal|"à¸à¸µà¹"
block|,
literal|"à¹à¸à¹"
block|,
literal|"à¸à¹à¸­à¸"
block|,
literal|"à¹à¸ªà¸à¸"
block|,
literal|"à¸§à¹à¸²"
block|,
literal|"à¸à¸²à¸"
block|,
literal|"à¸à¸µ"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|3
block|,
literal|6
block|,
literal|9
block|,
literal|13
block|,
literal|17
block|,
literal|20
block|,
literal|23
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|6
block|,
literal|9
block|,
literal|13
block|,
literal|17
block|,
literal|20
block|,
literal|23
block|,
literal|25
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testStopWords
specifier|public
name|void
name|testStopWords
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
operator|new
name|ThaiAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|,
literal|"à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à¹à¸ªà¸à¸"
block|,
literal|"à¸à¸²à¸"
block|,
literal|"à¸à¸µ"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|13
block|,
literal|20
block|,
literal|23
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|17
block|,
literal|23
block|,
literal|25
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|5
block|,
literal|2
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
comment|/*    * Test that position increments are adjusted correctly for stopwords.    */
comment|// note this test uses stopfilter's stopset
DECL|method|testPositionIncrements
specifier|public
name|void
name|testPositionIncrements
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|ThaiAnalyzer
name|analyzer
init|=
operator|new
name|ThaiAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|StopAnalyzer
operator|.
name|ENGLISH_STOP_WORDS_SET
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸ the à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à¸à¸²à¸£"
block|,
literal|"à¸à¸µà¹"
block|,
literal|"à¹à¸à¹"
block|,
literal|"à¸à¹à¸­à¸"
block|,
literal|"à¹à¸ªà¸à¸"
block|,
literal|"à¸§à¹à¸²"
block|,
literal|"à¸à¸²à¸"
block|,
literal|"à¸à¸µ"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|3
block|,
literal|6
block|,
literal|9
block|,
literal|18
block|,
literal|22
block|,
literal|25
block|,
literal|28
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|6
block|,
literal|9
block|,
literal|13
block|,
literal|22
block|,
literal|25
block|,
literal|28
block|,
literal|30
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
literal|2
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
comment|// case that a stopword is adjacent to thai text, with no whitespace
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸the à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à¸à¸²à¸£"
block|,
literal|"à¸à¸µà¹"
block|,
literal|"à¹à¸à¹"
block|,
literal|"à¸à¹à¸­à¸"
block|,
literal|"à¹à¸ªà¸à¸"
block|,
literal|"à¸§à¹à¸²"
block|,
literal|"à¸à¸²à¸"
block|,
literal|"à¸à¸µ"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|3
block|,
literal|6
block|,
literal|9
block|,
literal|17
block|,
literal|21
block|,
literal|24
block|,
literal|27
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|6
block|,
literal|9
block|,
literal|13
block|,
literal|21
block|,
literal|24
block|,
literal|27
block|,
literal|29
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
literal|2
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
name|Exception
block|{
name|ThaiAnalyzer
name|analyzer
init|=
operator|new
name|ThaiAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|CharArraySet
operator|.
name|EMPTY_SET
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|""
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à¸à¸²à¸£"
block|,
literal|"à¸à¸µà¹"
block|,
literal|"à¹à¸à¹"
block|,
literal|"à¸à¹à¸­à¸"
block|,
literal|"à¹à¸ªà¸à¸"
block|,
literal|"à¸§à¹à¸²"
block|,
literal|"à¸à¸²à¸"
block|,
literal|"à¸à¸µ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"à¸à¸£à¸´à¸©à¸±à¸à¸à¸·à¹à¸­ XY&Z - à¸à¸¸à¸¢à¸à¸±à¸ xyz@demo.com"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à¸à¸£à¸´à¸©à¸±à¸"
block|,
literal|"à¸à¸·à¹à¸­"
block|,
literal|"xy"
block|,
literal|"z"
block|,
literal|"à¸à¸¸à¸¢"
block|,
literal|"à¸à¸±à¸"
block|,
literal|"xyz"
block|,
literal|"demo.com"
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
name|ThaiAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|,
literal|1000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
block|}
comment|/** blast some random large strings through the analyzer */
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
name|ThaiAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|,
literal|100
operator|*
name|RANDOM_MULTIPLIER
argument_list|,
literal|8192
argument_list|)
expr_stmt|;
block|}
comment|// LUCENE-3044
DECL|method|testAttributeReuse
specifier|public
name|void
name|testAttributeReuse
parameter_list|()
throws|throws
name|Exception
block|{
name|ThaiAnalyzer
name|analyzer
init|=
operator|new
name|ThaiAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
comment|// just consume
name|TokenStream
name|ts
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|"dummy"
argument_list|,
literal|"à¸ à¸²à¸©à¸²à¹à¸à¸¢"
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à¸ à¸²à¸©à¸²"
block|,
literal|"à¹à¸à¸¢"
block|}
argument_list|)
expr_stmt|;
comment|// this consumer adds flagsAtt, which this analyzer does not use.
name|ts
operator|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|"dummy"
argument_list|,
literal|"à¸ à¸²à¸©à¸²à¹à¸à¸¢"
argument_list|)
expr_stmt|;
name|ts
operator|.
name|addAttribute
argument_list|(
name|FlagsAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à¸ à¸²à¸©à¸²"
block|,
literal|"à¹à¸à¸¢"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
