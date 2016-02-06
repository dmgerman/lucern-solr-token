begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.icu.segmentation
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
operator|.
name|segmentation
package|;
end_package
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
begin_comment
comment|/**  * test ICUTokenizer with dictionary-based CJ segmentation  */
end_comment
begin_class
DECL|class|TestICUTokenizerCJK
specifier|public
class|class
name|TestICUTokenizerCJK
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|a
name|Analyzer
name|a
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
name|ICUTokenizer
argument_list|(
name|newAttributeFactory
argument_list|()
argument_list|,
operator|new
name|DefaultICUTokenizerConfig
argument_list|(
literal|true
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|a
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|/**    * test stolen from smartcn    */
DECL|method|testSimpleChinese
specifier|public
name|void
name|testSimpleChinese
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"æè´­ä¹°äºéå·åæè£ã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"æ"
block|,
literal|"è´­ä¹°"
block|,
literal|"äº"
block|,
literal|"éå·"
block|,
literal|"å"
block|,
literal|"æè£"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testChineseNumerics
specifier|public
name|void
name|testChineseNumerics
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ï¼ï¼ï¼ï¼"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ï¼ï¼ï¼ï¼"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"é¢å§åæ©ï¼ï¼ï¼ï¼ã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"é¢"
block|,
literal|"å§"
block|,
literal|"åæ©"
block|,
literal|"ï¼ï¼ï¼ï¼"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"é¢å§åæ©9483ã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"é¢"
block|,
literal|"å§"
block|,
literal|"åæ©"
block|,
literal|"9483"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * test stolen from kuromoji    */
DECL|method|testSimpleJapanese
specifier|public
name|void
name|testSimpleJapanese
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ããã¯ã¾ã å®é¨æ®µéã«ããã¾ã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ãã"
block|,
literal|"ã¯"
block|,
literal|"ã¾ã "
block|,
literal|"å®é¨"
block|,
literal|"æ®µé"
block|,
literal|"ã«"
block|,
literal|"ãã"
block|,
literal|"ã¾ã"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testJapaneseTypes
specifier|public
name|void
name|testJapaneseTypes
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ä»®åé£ã ã«ã¿ã«ã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ä»®åé£ã"
block|,
literal|"ã«ã¿ã«ã"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<IDEOGRAPHIC>"
block|,
literal|"<IDEOGRAPHIC>"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testKorean
specifier|public
name|void
name|testKorean
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Korean words
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ìëíì¸ì íê¸ìëë¤"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ìëíì¸ì"
block|,
literal|"íê¸ìëë¤"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** make sure that we still tag korean as HANGUL (for further decomposition/ngram/whatever) */
DECL|method|testKoreanTypes
specifier|public
name|void
name|testKoreanTypes
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"íë¯¼ì ì"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"íë¯¼ì ì"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<HANGUL>"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** blast some random strings through the analyzer */
annotation|@
name|AwaitsFix
argument_list|(
name|bugUrl
operator|=
literal|"https://issues.apache.org/jira/browse/LUCENE-5575"
argument_list|)
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
name|a
argument_list|,
literal|10000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
block|}
comment|/** blast some random large strings through the analyzer */
annotation|@
name|AwaitsFix
argument_list|(
name|bugUrl
operator|=
literal|"https://issues.apache.org/jira/browse/LUCENE-5575"
argument_list|)
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
block|}
end_class
end_unit
