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
name|StringReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|java
operator|.
name|util
operator|.
name|Map
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
name|solr
operator|.
name|core
operator|.
name|SolrResourceLoader
import|;
end_import
begin_comment
comment|/**  * Simple tests for {@link JapaneseTokenizerFactory}  */
end_comment
begin_class
DECL|class|TestJapaneseTokenizerFactory
specifier|public
class|class
name|TestJapaneseTokenizerFactory
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|IOException
block|{
name|JapaneseTokenizerFactory
name|factory
init|=
operator|new
name|JapaneseTokenizerFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setLuceneMatchVersion
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
name|Collections
operator|.
name|emptyMap
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|factory
operator|.
name|inform
argument_list|(
operator|new
name|SolrResourceLoader
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|TokenStream
name|ts
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"ããã¯æ¬ã§ã¯ãªã"
argument_list|)
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
literal|"ãã"
block|,
literal|"ã¯"
block|,
literal|"æ¬"
block|,
literal|"ã§"
block|,
literal|"ã¯"
block|,
literal|"ãªã"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|,
literal|6
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|,
literal|6
block|,
literal|8
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that search mode is enabled and working by default    */
DECL|method|testDefaults
specifier|public
name|void
name|testDefaults
parameter_list|()
throws|throws
name|IOException
block|{
name|JapaneseTokenizerFactory
name|factory
init|=
operator|new
name|JapaneseTokenizerFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setLuceneMatchVersion
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
name|Collections
operator|.
name|emptyMap
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|factory
operator|.
name|inform
argument_list|(
operator|new
name|SolrResourceLoader
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|TokenStream
name|ts
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"ã·ãã¢ã½ããã¦ã§ã¢ã¨ã³ã¸ãã¢"
argument_list|)
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
literal|"ã·ãã¢"
block|,
literal|"ã·ãã¢ã½ããã¦ã§ã¢ã¨ã³ã¸ãã¢"
block|,
literal|"ã½ããã¦ã§ã¢"
block|,
literal|"ã¨ã³ã¸ãã¢"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test mode parameter: specifying normal mode    */
DECL|method|testMode
specifier|public
name|void
name|testMode
parameter_list|()
throws|throws
name|IOException
block|{
name|JapaneseTokenizerFactory
name|factory
init|=
operator|new
name|JapaneseTokenizerFactory
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"mode"
argument_list|,
literal|"normal"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|factory
operator|.
name|inform
argument_list|(
operator|new
name|SolrResourceLoader
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|TokenStream
name|ts
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"ã·ãã¢ã½ããã¦ã§ã¢ã¨ã³ã¸ãã¢"
argument_list|)
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
literal|"ã·ãã¢ã½ããã¦ã§ã¢ã¨ã³ã¸ãã¢"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test user dictionary    */
DECL|method|testUserDict
specifier|public
name|void
name|testUserDict
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|userDict
init|=
literal|"# Custom segmentation for long entries\n"
operator|+
literal|"æ¥æ¬çµæ¸æ°è,æ¥æ¬ çµæ¸ æ°è,ããã³ ã±ã¤ã¶ã¤ ã·ã³ãã³,ã«ã¹ã¿ã åè©\n"
operator|+
literal|"é¢è¥¿å½éç©ºæ¸¯,é¢è¥¿ å½é ç©ºæ¸¯,ã«ã³ãµã¤ ã³ã¯ãµã¤ ã¯ã¦ã³ã¦,ãã¹ãåè©\n"
operator|+
literal|"# Custom reading for sumo wrestler\n"
operator|+
literal|"æéé¾,æéé¾,ã¢ãµã·ã§ã¦ãªã¥ã¦,ã«ã¹ã¿ã äººå\n"
decl_stmt|;
name|JapaneseTokenizerFactory
name|factory
init|=
operator|new
name|JapaneseTokenizerFactory
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"userDictionary"
argument_list|,
literal|"userdict.txt"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|factory
operator|.
name|inform
argument_list|(
operator|new
name|StringMockSolrResourceLoader
argument_list|(
name|userDict
argument_list|)
argument_list|)
expr_stmt|;
name|TokenStream
name|ts
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"é¢è¥¿å½éç©ºæ¸¯ã«è¡ã£ã"
argument_list|)
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
literal|"é¢è¥¿"
block|,
literal|"å½é"
block|,
literal|"ç©ºæ¸¯"
block|,
literal|"ã«"
block|,
literal|"è¡ã£"
block|,
literal|"ã"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test preserving punctuation    */
DECL|method|testPreservePunctuation
specifier|public
name|void
name|testPreservePunctuation
parameter_list|()
throws|throws
name|IOException
block|{
name|JapaneseTokenizerFactory
name|factory
init|=
operator|new
name|JapaneseTokenizerFactory
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"discardPunctuation"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|factory
operator|.
name|inform
argument_list|(
operator|new
name|SolrResourceLoader
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|TokenStream
name|ts
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"ä»ãã«ã¦ã§ã¼ã«ãã¾ãããæ¥é±ã®é ­æ¥æ¬ã«æ»ãã¾ããæ¥½ãã¿ã«ãã¦ãã¾ãï¼ãå¯¿å¸ãé£ã¹ãããªããã"
argument_list|)
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|ts
operator|.
name|toString
argument_list|()
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
literal|"ä»"
block|,
literal|"ãã«ã¦ã§ã¼"
block|,
literal|"ã«"
block|,
literal|"ã"
block|,
literal|"ã¾ã"
block|,
literal|"ã"
block|,
literal|"ã"
block|,
literal|"æ¥é±"
block|,
literal|"ã®"
block|,
literal|"é ­"
block|,
literal|"æ¥æ¬"
block|,
literal|"ã«"
block|,
literal|"æ»ã"
block|,
literal|"ã¾ã"
block|,
literal|"ã"
block|,
literal|"æ¥½ãã¿"
block|,
literal|"ã«"
block|,
literal|"ã"
block|,
literal|"ã¦"
block|,
literal|"ã"
block|,
literal|"ã¾ã"
block|,
literal|"ï¼"
block|,
literal|"ã"
block|,
literal|"å¯¿å¸"
block|,
literal|"ã"
block|,
literal|"é£ã¹"
block|,
literal|"ãã"
block|,
literal|"ãª"
block|,
literal|"ã"
block|,
literal|"ã"
block|,
literal|"ã"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
