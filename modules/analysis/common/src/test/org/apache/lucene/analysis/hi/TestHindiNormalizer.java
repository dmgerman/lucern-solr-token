begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.hi
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|hi
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
name|Tokenizer
import|;
end_import
begin_comment
comment|/**  * Test HindiNormalizer  */
end_comment
begin_class
DECL|class|TestHindiNormalizer
specifier|public
class|class
name|TestHindiNormalizer
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/**    * Test some basic normalization, with an example from the paper.    */
DECL|method|testBasics
specifier|public
name|void
name|testBasics
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"à¤à¤à¤à¤°à¥à¤à¤¼à¥"
argument_list|,
literal|"à¤à¤à¤à¤°à¥à¤à¤¿"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"à¤à¤à¤à¤°à¥à¤à¥"
argument_list|,
literal|"à¤à¤à¤à¤°à¥à¤à¤¿"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"à¤à¤à¤à¥à¤°à¥à¤à¤¼à¥"
argument_list|,
literal|"à¤à¤à¤à¤°à¥à¤à¤¿"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"à¤à¤à¤à¥à¤°à¥à¤à¥"
argument_list|,
literal|"à¤à¤à¤à¤°à¥à¤à¤¿"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"à¤à¤à¤à¤°à¥à¤à¤¼à¥"
argument_list|,
literal|"à¤à¤à¤à¤°à¥à¤à¤¿"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"à¤à¤à¤à¤°à¥à¤à¥"
argument_list|,
literal|"à¤à¤à¤à¤°à¥à¤à¤¿"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"à¤à¤à¤à¥à¤°à¥à¤à¤¼à¥"
argument_list|,
literal|"à¤à¤à¤à¤°à¥à¤à¤¿"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"à¤à¤à¤à¥à¤°à¥à¤à¥"
argument_list|,
literal|"à¤à¤à¤à¤°à¥à¤à¤¿"
argument_list|)
expr_stmt|;
block|}
DECL|method|testDecompositions
specifier|public
name|void
name|testDecompositions
parameter_list|()
throws|throws
name|IOException
block|{
comment|// removing nukta dot
name|check
argument_list|(
literal|"à¥à¤¿à¤¤à¤¾à¤¬"
argument_list|,
literal|"à¤à¤¿à¤¤à¤¾à¤¬"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"à¥à¤°à¥à¥"
argument_list|,
literal|"à¤«à¤°à¤"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"à¥à¤°à¥à¥"
argument_list|,
literal|"à¤à¤°à¤"
argument_list|)
expr_stmt|;
comment|// some other composed nukta forms
name|check
argument_list|(
literal|"à¤±à¤´à¥à¥à¥à¥à¥"
argument_list|,
literal|"à¤°à¤³à¤à¤à¤¡à¤¢à¤¯"
argument_list|)
expr_stmt|;
comment|// removal of format (ZWJ/ZWNJ)
name|check
argument_list|(
literal|"à¤¶à¤¾à¤°à¥âà¤®à¤¾"
argument_list|,
literal|"à¤¶à¤¾à¤°à¤®à¤¾"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"à¤¶à¤¾à¤°à¥âà¤®à¤¾"
argument_list|,
literal|"à¤¶à¤¾à¤°à¤®à¤¾"
argument_list|)
expr_stmt|;
comment|// removal of chandra
name|check
argument_list|(
literal|"à¥à¥à¥à¥à¤à¤à¤à¤\u0972"
argument_list|,
literal|"à¥à¥à¥à¥à¤à¤à¤à¤à¤"
argument_list|)
expr_stmt|;
comment|// vowel shortening
name|check
argument_list|(
literal|"à¤à¤à¤à¥ à¥¡à¤à¤à¥à¥à¥à¥£à¥à¥"
argument_list|,
literal|"à¤à¤à¤à¤à¤à¤à¤à¤¿à¥à¥à¥¢à¥à¥"
argument_list|)
expr_stmt|;
block|}
DECL|method|check
specifier|private
name|void
name|check
parameter_list|(
name|String
name|input
parameter_list|,
name|String
name|output
parameter_list|)
throws|throws
name|IOException
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|TokenFilter
name|tf
init|=
operator|new
name|HindiNormalizationFilter
argument_list|(
name|tokenizer
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tf
argument_list|,
operator|new
name|String
index|[]
block|{
name|output
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
