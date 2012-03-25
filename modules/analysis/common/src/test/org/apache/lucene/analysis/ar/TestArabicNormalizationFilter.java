begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.ar
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ar
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
begin_comment
comment|/**  * Test the Arabic Normalization Filter  */
end_comment
begin_class
DECL|class|TestArabicNormalizationFilter
specifier|public
class|class
name|TestArabicNormalizationFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testAlifMadda
specifier|public
name|void
name|testAlifMadda
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"Ø¢Ø¬Ù"
argument_list|,
literal|"Ø§Ø¬Ù"
argument_list|)
expr_stmt|;
block|}
DECL|method|testAlifHamzaAbove
specifier|public
name|void
name|testAlifHamzaAbove
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"Ø£Ø­ÙØ¯"
argument_list|,
literal|"Ø§Ø­ÙØ¯"
argument_list|)
expr_stmt|;
block|}
DECL|method|testAlifHamzaBelow
specifier|public
name|void
name|testAlifHamzaBelow
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"Ø¥Ø¹Ø§Ø°"
argument_list|,
literal|"Ø§Ø¹Ø§Ø°"
argument_list|)
expr_stmt|;
block|}
DECL|method|testAlifMaksura
specifier|public
name|void
name|testAlifMaksura
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"Ø¨ÙÙ"
argument_list|,
literal|"Ø¨ÙÙ"
argument_list|)
expr_stmt|;
block|}
DECL|method|testTehMarbuta
specifier|public
name|void
name|testTehMarbuta
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"ÙØ§Ø·ÙØ©"
argument_list|,
literal|"ÙØ§Ø·ÙÙ"
argument_list|)
expr_stmt|;
block|}
DECL|method|testTatweel
specifier|public
name|void
name|testTatweel
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"Ø±ÙØ¨Ø±ÙÙÙÙÙØª"
argument_list|,
literal|"Ø±ÙØ¨Ø±Øª"
argument_list|)
expr_stmt|;
block|}
DECL|method|testFatha
specifier|public
name|void
name|testFatha
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"ÙÙØ¨ÙØ§"
argument_list|,
literal|"ÙØ¨ÙØ§"
argument_list|)
expr_stmt|;
block|}
DECL|method|testKasra
specifier|public
name|void
name|testKasra
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"Ø¹ÙÙÙ"
argument_list|,
literal|"Ø¹ÙÙ"
argument_list|)
expr_stmt|;
block|}
DECL|method|testDamma
specifier|public
name|void
name|testDamma
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"Ø¨ÙÙØ§Øª"
argument_list|,
literal|"Ø¨ÙØ§Øª"
argument_list|)
expr_stmt|;
block|}
DECL|method|testFathatan
specifier|public
name|void
name|testFathatan
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"ÙÙØ¯Ø§Ù"
argument_list|,
literal|"ÙÙØ¯Ø§"
argument_list|)
expr_stmt|;
block|}
DECL|method|testKasratan
specifier|public
name|void
name|testKasratan
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"ÙÙØ¯Ù"
argument_list|,
literal|"ÙÙØ¯"
argument_list|)
expr_stmt|;
block|}
DECL|method|testDammatan
specifier|public
name|void
name|testDammatan
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"ÙÙØ¯Ù"
argument_list|,
literal|"ÙÙØ¯"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSukun
specifier|public
name|void
name|testSukun
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"ÙÙÙØ³ÙÙ"
argument_list|,
literal|"ÙÙØ³ÙÙ"
argument_list|)
expr_stmt|;
block|}
DECL|method|testShaddah
specifier|public
name|void
name|testShaddah
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"ÙØªÙÙÙ"
argument_list|,
literal|"ÙØªÙÙ"
argument_list|)
expr_stmt|;
block|}
DECL|method|check
specifier|private
name|void
name|check
parameter_list|(
specifier|final
name|String
name|input
parameter_list|,
specifier|final
name|String
name|expected
parameter_list|)
throws|throws
name|IOException
block|{
name|ArabicLetterTokenizer
name|tokenStream
init|=
operator|new
name|ArabicLetterTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
decl_stmt|;
name|ArabicNormalizationFilter
name|filter
init|=
operator|new
name|ArabicNormalizationFilter
argument_list|(
name|tokenStream
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
name|expected
block|}
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
name|ArabicNormalizationFilter
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
