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
name|Tokenizer
import|;
end_import
begin_comment
comment|/**  * A few tests based on  org.apache.lucene.analysis.TestUAX29Tokenizer;  */
end_comment
begin_class
DECL|class|TestUAX29TokenizerFactory
specifier|public
class|class
name|TestUAX29TokenizerFactory
extends|extends
name|BaseTokenTestCase
block|{
comment|/**    * Test UAX29TokenizerFactory    */
DECL|method|testUAX29Tokenizer
specifier|public
name|void
name|testUAX29Tokenizer
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
name|UAX29TokenizerFactory
name|factory
init|=
operator|new
name|UAX29TokenizerFactory
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
DECL|method|testArabic
specifier|public
name|void
name|testArabic
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
literal|"Ø§ÙÙÙÙÙ Ø§ÙÙØ«Ø§Ø¦ÙÙ Ø§ÙØ£ÙÙ Ø¹Ù ÙÙÙÙØ¨ÙØ¯ÙØ§ ÙØ³ÙÙ \"Ø§ÙØ­ÙÙÙØ© Ø¨Ø§ÙØ£Ø±ÙØ§Ù: ÙØµØ© ÙÙÙÙØ¨ÙØ¯ÙØ§\" (Ø¨Ø§ÙØ¥ÙØ¬ÙÙØ²ÙØ©: Truth in Numbers: The Wikipedia Story)Ø Ø³ÙØªÙ Ø¥Ø·ÙØ§ÙÙ ÙÙ 2008."
argument_list|)
decl_stmt|;
name|UAX29TokenizerFactory
name|factory
init|=
operator|new
name|UAX29TokenizerFactory
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
literal|"Ø§ÙÙÙÙÙ"
block|,
literal|"Ø§ÙÙØ«Ø§Ø¦ÙÙ"
block|,
literal|"Ø§ÙØ£ÙÙ"
block|,
literal|"Ø¹Ù"
block|,
literal|"ÙÙÙÙØ¨ÙØ¯ÙØ§"
block|,
literal|"ÙØ³ÙÙ"
block|,
literal|"Ø§ÙØ­ÙÙÙØ©"
block|,
literal|"Ø¨Ø§ÙØ£Ø±ÙØ§Ù"
block|,
literal|"ÙØµØ©"
block|,
literal|"ÙÙÙÙØ¨ÙØ¯ÙØ§"
block|,
literal|"Ø¨Ø§ÙØ¥ÙØ¬ÙÙØ²ÙØ©"
block|,
literal|"Truth"
block|,
literal|"in"
block|,
literal|"Numbers"
block|,
literal|"The"
block|,
literal|"Wikipedia"
block|,
literal|"Story"
block|,
literal|"Ø³ÙØªÙ"
block|,
literal|"Ø¥Ø·ÙØ§ÙÙ"
block|,
literal|"ÙÙ"
block|,
literal|"2008"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testChinese
specifier|public
name|void
name|testChinese
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
literal|"ææ¯ä¸­å½äººã ï¼ï¼ï¼ï¼ ï¼´ï½ï½ï½ï½ "
argument_list|)
decl_stmt|;
name|UAX29TokenizerFactory
name|factory
init|=
operator|new
name|UAX29TokenizerFactory
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
literal|"æ"
block|,
literal|"æ¯"
block|,
literal|"ä¸­"
block|,
literal|"å½"
block|,
literal|"äºº"
block|,
literal|"ï¼ï¼ï¼ï¼"
block|,
literal|"ï¼´ï½ï½ï½ï½"
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
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"ìëíì¸ì íê¸ìëë¤"
argument_list|)
decl_stmt|;
name|UAX29TokenizerFactory
name|factory
init|=
operator|new
name|UAX29TokenizerFactory
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
literal|"ìëíì¸ì"
block|,
literal|"íê¸ìëë¤"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testHyphen
specifier|public
name|void
name|testHyphen
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
literal|"some-dashed-phrase"
argument_list|)
decl_stmt|;
name|UAX29TokenizerFactory
name|factory
init|=
operator|new
name|UAX29TokenizerFactory
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
literal|"some"
block|,
literal|"dashed"
block|,
literal|"phrase"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
