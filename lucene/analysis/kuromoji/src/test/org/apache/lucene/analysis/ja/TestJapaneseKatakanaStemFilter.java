begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.ja
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ja
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
name|miscellaneous
operator|.
name|SetKeywordMarkerFilter
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
begin_comment
comment|/**  * Tests for {@link JapaneseKatakanaStemFilter}  */
end_comment
begin_class
DECL|class|TestJapaneseKatakanaStemFilter
specifier|public
class|class
name|TestJapaneseKatakanaStemFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|analyzer
specifier|private
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
comment|// Use a MockTokenizer here since this filter doesn't really depend on Kuromoji
name|Tokenizer
name|source
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
name|source
argument_list|,
operator|new
name|JapaneseKatakanaStemFilter
argument_list|(
name|source
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/**    * Test a few common katakana spelling variations.    *<p>    * English translations are as follows:    *<ul>    *<li>copy</li>    *<li>coffee</li>    *<li>taxi</li>    *<li>party</li>    *<li>party (without long sound)</li>    *<li>center</li>    *</ul>    * Note that we remove a long sound in the case of "coffee" that is required.    *</p>    */
DECL|method|testStemVariants
specifier|public
name|void
name|testStemVariants
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ã³ãã¼ ã³ã¼ãã¼ ã¿ã¯ã·ã¼ ãã¼ãã£ã¼ ãã¼ãã£ ã»ã³ã¿ã¼"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ã³ãã¼"
block|,
literal|"ã³ã¼ã"
block|,
literal|"ã¿ã¯ã·"
block|,
literal|"ãã¼ãã£"
block|,
literal|"ãã¼ãã£"
block|,
literal|"ã»ã³ã¿"
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
literal|9
block|,
literal|14
block|,
literal|20
block|,
literal|25
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|8
block|,
literal|13
block|,
literal|19
block|,
literal|24
block|,
literal|29
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testKeyword
specifier|public
name|void
name|testKeyword
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|CharArraySet
name|exclusionSet
init|=
operator|new
name|CharArraySet
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|asSet
argument_list|(
literal|"ã³ã¼ãã¼"
argument_list|)
argument_list|,
literal|false
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
name|source
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
name|TokenStream
name|sink
init|=
operator|new
name|SetKeywordMarkerFilter
argument_list|(
name|source
argument_list|,
name|exclusionSet
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|source
argument_list|,
operator|new
name|JapaneseKatakanaStemFilter
argument_list|(
name|sink
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"ã³ã¼ãã¼"
argument_list|,
literal|"ã³ã¼ãã¼"
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnsupportedHalfWidthVariants
specifier|public
name|void
name|testUnsupportedHalfWidthVariants
parameter_list|()
throws|throws
name|IOException
block|{
comment|// The below result is expected since only full-width katakana is supported
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ï¾ï½¸ï½¼ï½°"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ï¾ï½¸ï½¼ï½°"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandomData
specifier|public
name|void
name|testRandomData
parameter_list|()
throws|throws
name|IOException
block|{
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|analyzer
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
name|JapaneseKatakanaStemFilter
argument_list|(
name|tokenizer
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|checkOneTerm
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
