begin_unit
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
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
name|WhitespaceTokenizer
import|;
end_import
begin_comment
comment|/**  * Simple tests to ensure the standard lucene factories are working.  */
end_comment
begin_class
DECL|class|TestStandardFactories
specifier|public
class|class
name|TestStandardFactories
extends|extends
name|BaseTokenTestCase
block|{
comment|/**    * Test StandardTokenizerFactory    */
DECL|method|testStandardTokenizer
specifier|public
name|void
name|testStandardTokenizer
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
literal|"Wha\u0301t's this thing do?"
argument_list|)
decl_stmt|;
name|StandardTokenizerFactory
name|factory
init|=
operator|new
name|StandardTokenizerFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|DEFAULT_VERSION_PARAM
argument_list|)
expr_stmt|;
name|Tokenizer
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Wha\u0301t's"
block|,
literal|"this"
block|,
literal|"thing"
block|,
literal|"do"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test ClassicTokenizerFactory    */
DECL|method|testClassicTokenizer
specifier|public
name|void
name|testClassicTokenizer
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
literal|"What's this thing do?"
argument_list|)
decl_stmt|;
name|ClassicTokenizerFactory
name|factory
init|=
operator|new
name|ClassicTokenizerFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|DEFAULT_VERSION_PARAM
argument_list|)
expr_stmt|;
name|Tokenizer
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"What's"
block|,
literal|"this"
block|,
literal|"thing"
block|,
literal|"do"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test ClassicFilterFactory    */
DECL|method|testStandardFilter
specifier|public
name|void
name|testStandardFilter
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
literal|"What's this thing do?"
argument_list|)
decl_stmt|;
name|ClassicTokenizerFactory
name|factory
init|=
operator|new
name|ClassicTokenizerFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|DEFAULT_VERSION_PARAM
argument_list|)
expr_stmt|;
name|ClassicFilterFactory
name|filterFactory
init|=
operator|new
name|ClassicFilterFactory
argument_list|()
decl_stmt|;
name|filterFactory
operator|.
name|init
argument_list|(
name|DEFAULT_VERSION_PARAM
argument_list|)
expr_stmt|;
name|Tokenizer
name|tokenizer
init|=
name|factory
operator|.
name|create
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
name|filterFactory
operator|.
name|create
argument_list|(
name|tokenizer
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"What"
block|,
literal|"this"
block|,
literal|"thing"
block|,
literal|"do"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test KeywordTokenizerFactory    */
DECL|method|testKeywordTokenizer
specifier|public
name|void
name|testKeywordTokenizer
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
literal|"What's this thing do?"
argument_list|)
decl_stmt|;
name|KeywordTokenizerFactory
name|factory
init|=
operator|new
name|KeywordTokenizerFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|DEFAULT_VERSION_PARAM
argument_list|)
expr_stmt|;
name|Tokenizer
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"What's this thing do?"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test WhitespaceTokenizerFactory    */
DECL|method|testWhitespaceTokenizer
specifier|public
name|void
name|testWhitespaceTokenizer
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
literal|"What's this thing do?"
argument_list|)
decl_stmt|;
name|WhitespaceTokenizerFactory
name|factory
init|=
operator|new
name|WhitespaceTokenizerFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|DEFAULT_VERSION_PARAM
argument_list|)
expr_stmt|;
name|Tokenizer
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"What's"
block|,
literal|"this"
block|,
literal|"thing"
block|,
literal|"do?"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test LetterTokenizerFactory    */
DECL|method|testLetterTokenizer
specifier|public
name|void
name|testLetterTokenizer
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
literal|"What's this thing do?"
argument_list|)
decl_stmt|;
name|LetterTokenizerFactory
name|factory
init|=
operator|new
name|LetterTokenizerFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|DEFAULT_VERSION_PARAM
argument_list|)
expr_stmt|;
name|Tokenizer
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"What"
block|,
literal|"s"
block|,
literal|"this"
block|,
literal|"thing"
block|,
literal|"do"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test LowerCaseTokenizerFactory    */
DECL|method|testLowerCaseTokenizer
specifier|public
name|void
name|testLowerCaseTokenizer
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
literal|"What's this thing do?"
argument_list|)
decl_stmt|;
name|LowerCaseTokenizerFactory
name|factory
init|=
operator|new
name|LowerCaseTokenizerFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|DEFAULT_VERSION_PARAM
argument_list|)
expr_stmt|;
name|Tokenizer
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"what"
block|,
literal|"s"
block|,
literal|"this"
block|,
literal|"thing"
block|,
literal|"do"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Ensure the ASCIIFoldingFilterFactory works    */
DECL|method|testASCIIFolding
specifier|public
name|void
name|testASCIIFolding
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
literal|"ÄeskÃ¡"
argument_list|)
decl_stmt|;
name|Tokenizer
name|tokenizer
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|ASCIIFoldingFilterFactory
name|factory
init|=
operator|new
name|ASCIIFoldingFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|DEFAULT_VERSION_PARAM
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
name|tokenizer
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ceska"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
