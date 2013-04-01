begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.miscellaneous
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|miscellaneous
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
name|util
operator|.
name|BaseTokenStreamFactoryTestCase
import|;
end_import
begin_class
DECL|class|TestLimitTokenPositionFilterFactory
specifier|public
class|class
name|TestLimitTokenPositionFilterFactory
extends|extends
name|BaseTokenStreamFactoryTestCase
block|{
DECL|method|testMaxPosition1
specifier|public
name|void
name|testMaxPosition1
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
literal|"A1 B2 C3 D4 E5 F6"
argument_list|)
decl_stmt|;
name|MockTokenizer
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
comment|// LimitTokenPositionFilter doesn't consume the entire stream that it wraps
name|tokenizer
operator|.
name|setEnableChecks
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
name|tokenizer
decl_stmt|;
name|stream
operator|=
name|tokenFilterFactory
argument_list|(
literal|"LimitTokenPosition"
argument_list|,
literal|"maxTokenPosition"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|create
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"A1"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testMissingParam
specifier|public
name|void
name|testMissingParam
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|tokenFilterFactory
argument_list|(
literal|"LimitTokenPosition"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"exception doesn't mention param: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|0
operator|<
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|indexOf
argument_list|(
name|LimitTokenPositionFilterFactory
operator|.
name|MAX_TOKEN_POSITION_KEY
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testMaxPosition1WithShingles
specifier|public
name|void
name|testMaxPosition1WithShingles
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
literal|"one two three four five"
argument_list|)
decl_stmt|;
name|MockTokenizer
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
comment|// LimitTokenPositionFilter doesn't consume the entire stream that it wraps
name|tokenizer
operator|.
name|setEnableChecks
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
name|tokenizer
decl_stmt|;
name|stream
operator|=
name|tokenFilterFactory
argument_list|(
literal|"Shingle"
argument_list|,
literal|"minShingleSize"
argument_list|,
literal|"2"
argument_list|,
literal|"maxShingleSize"
argument_list|,
literal|"3"
argument_list|,
literal|"outputUnigrams"
argument_list|,
literal|"true"
argument_list|)
operator|.
name|create
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|stream
operator|=
name|tokenFilterFactory
argument_list|(
literal|"LimitTokenPosition"
argument_list|,
literal|"maxTokenPosition"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|create
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"one"
block|,
literal|"one two"
block|,
literal|"one two three"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testConsumeAllTokens
specifier|public
name|void
name|testConsumeAllTokens
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
literal|"A1 B2 C3 D4 E5 F6"
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
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
name|stream
operator|=
name|tokenFilterFactory
argument_list|(
literal|"LimitTokenPosition"
argument_list|,
literal|"maxTokenPosition"
argument_list|,
literal|"3"
argument_list|,
literal|"consumeAllTokens"
argument_list|,
literal|"true"
argument_list|)
operator|.
name|create
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"A1"
block|,
literal|"B2"
block|,
literal|"C3"
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
name|tokenFilterFactory
argument_list|(
literal|"LimitTokenPosition"
argument_list|,
literal|"maxTokenPosition"
argument_list|,
literal|"3"
argument_list|,
literal|"bogusArg"
argument_list|,
literal|"bogusValue"
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
