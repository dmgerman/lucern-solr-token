begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.fa
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|fa
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
name|ar
operator|.
name|ArabicLetterTokenizer
import|;
end_import
begin_comment
comment|/**  * Test the Persian Normalization Filter  *   */
end_comment
begin_class
DECL|class|TestPersianNormalizationFilter
specifier|public
class|class
name|TestPersianNormalizationFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testFarsiYeh
specifier|public
name|void
name|testFarsiYeh
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"ÙØ§Û"
argument_list|,
literal|"ÙØ§Ù"
argument_list|)
expr_stmt|;
block|}
DECL|method|testYehBarree
specifier|public
name|void
name|testYehBarree
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"ÙØ§Û"
argument_list|,
literal|"ÙØ§Ù"
argument_list|)
expr_stmt|;
block|}
DECL|method|testKeheh
specifier|public
name|void
name|testKeheh
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"Ú©Ø´Ø§ÙØ¯Ù"
argument_list|,
literal|"ÙØ´Ø§ÙØ¯Ù"
argument_list|)
expr_stmt|;
block|}
DECL|method|testHehYeh
specifier|public
name|void
name|testHehYeh
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"ÙØªØ§Ø¨Û"
argument_list|,
literal|"ÙØªØ§Ø¨Ù"
argument_list|)
expr_stmt|;
block|}
DECL|method|testHehHamzaAbove
specifier|public
name|void
name|testHehHamzaAbove
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"ÙØªØ§Ø¨ÙÙ"
argument_list|,
literal|"ÙØªØ§Ø¨Ù"
argument_list|)
expr_stmt|;
block|}
DECL|method|testHehGoal
specifier|public
name|void
name|testHehGoal
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"Ø²Ø§Ø¯Û"
argument_list|,
literal|"Ø²Ø§Ø¯Ù"
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
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
decl_stmt|;
name|PersianNormalizationFilter
name|filter
init|=
operator|new
name|PersianNormalizationFilter
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
block|}
end_class
end_unit
