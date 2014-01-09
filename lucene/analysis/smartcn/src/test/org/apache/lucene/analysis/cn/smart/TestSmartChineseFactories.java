begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.cn.smart
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cn
operator|.
name|smart
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
name|HashMap
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
begin_comment
comment|/**   * Tests for {@link SmartChineseSentenceTokenizerFactory} and   * {@link SmartChineseWordTokenFilterFactory}  */
end_comment
begin_class
DECL|class|TestSmartChineseFactories
specifier|public
class|class
name|TestSmartChineseFactories
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/** Test showing the behavior with whitespace */
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"æè´­ä¹°äºéå·åæè£ã"
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
name|whitespaceMockTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|SmartChineseWordTokenFilterFactory
name|factory
init|=
operator|new
name|SmartChineseWordTokenFilterFactory
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|stream
operator|=
name|factory
operator|.
name|create
argument_list|(
name|stream
argument_list|)
expr_stmt|;
comment|// TODO: fix smart chinese to not emit punctuation tokens
comment|// at the moment: you have to clean up with WDF, or use the stoplist, etc
name|assertTokenStreamContents
argument_list|(
name|stream
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
block|,
literal|","
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Test showing the behavior with whitespace */
DECL|method|testTokenizer
specifier|public
name|void
name|testTokenizer
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"æè´­ä¹°äºéå·åæè£ãæè´­ä¹°äºéå·åæè£ã"
argument_list|)
decl_stmt|;
name|SmartChineseSentenceTokenizerFactory
name|tokenizerFactory
init|=
operator|new
name|SmartChineseSentenceTokenizerFactory
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
name|tokenizerFactory
operator|.
name|create
argument_list|()
decl_stmt|;
operator|(
operator|(
name|Tokenizer
operator|)
name|stream
operator|)
operator|.
name|setReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|SmartChineseWordTokenFilterFactory
name|factory
init|=
operator|new
name|SmartChineseWordTokenFilterFactory
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|stream
operator|=
name|factory
operator|.
name|create
argument_list|(
name|stream
argument_list|)
expr_stmt|;
comment|// TODO: fix smart chinese to not emit punctuation tokens
comment|// at the moment: you have to clean up with WDF, or use the stoplist, etc
name|assertTokenStreamContents
argument_list|(
name|stream
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
block|,
literal|","
block|,
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
block|,
literal|","
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Test that bogus arguments result in exception */
DECL|method|testBogusArguments
specifier|public
name|void
name|testBogusArguments
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
operator|new
name|SmartChineseSentenceTokenizerFactory
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
literal|"bogusArg"
argument_list|,
literal|"bogusValue"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Unknown parameters"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
operator|new
name|SmartChineseWordTokenFilterFactory
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
literal|"bogusArg"
argument_list|,
literal|"bogusValue"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Unknown parameters"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
