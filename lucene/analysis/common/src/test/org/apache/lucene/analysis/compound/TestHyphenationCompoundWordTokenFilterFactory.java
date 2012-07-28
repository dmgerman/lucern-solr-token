begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.compound
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|compound
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
name|util
operator|.
name|ClasspathResourceLoader
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
name|ResourceLoader
import|;
end_import
begin_comment
comment|/**  * Simple tests to ensure the Hyphenation compound filter factory is working.  */
end_comment
begin_class
DECL|class|TestHyphenationCompoundWordTokenFilterFactory
specifier|public
class|class
name|TestHyphenationCompoundWordTokenFilterFactory
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/**    * Ensure the factory works with hyphenation grammar+dictionary: using default options.    */
DECL|method|testHyphenationWithDictionary
specifier|public
name|void
name|testHyphenationWithDictionary
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
literal|"min veninde som er lidt af en lÃ¦sehest"
argument_list|)
decl_stmt|;
name|Tokenizer
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
name|HyphenationCompoundWordTokenFilterFactory
name|factory
init|=
operator|new
name|HyphenationCompoundWordTokenFilterFactory
argument_list|()
decl_stmt|;
name|ResourceLoader
name|loader
init|=
operator|new
name|ClasspathResourceLoader
argument_list|(
name|getClass
argument_list|()
argument_list|)
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
literal|"hyphenator"
argument_list|,
literal|"da_UTF8.xml"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"dictionary"
argument_list|,
literal|"da_compoundDictionary.txt"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setLuceneMatchVersion
argument_list|(
name|TEST_VERSION_CURRENT
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
name|loader
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
literal|"min"
block|,
literal|"veninde"
block|,
literal|"som"
block|,
literal|"er"
block|,
literal|"lidt"
block|,
literal|"af"
block|,
literal|"en"
block|,
literal|"lÃ¦sehest"
block|,
literal|"lÃ¦se"
block|,
literal|"hest"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Ensure the factory works with no dictionary: using hyphenation grammar only.    * Also change the min/max subword sizes from the default. When using no dictionary,    * its generally necessary to tweak these, or you get lots of expansions.    */
DECL|method|testHyphenationOnly
specifier|public
name|void
name|testHyphenationOnly
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
literal|"basketballkurv"
argument_list|)
decl_stmt|;
name|Tokenizer
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
name|HyphenationCompoundWordTokenFilterFactory
name|factory
init|=
operator|new
name|HyphenationCompoundWordTokenFilterFactory
argument_list|()
decl_stmt|;
name|ResourceLoader
name|loader
init|=
operator|new
name|ClasspathResourceLoader
argument_list|(
name|getClass
argument_list|()
argument_list|)
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
literal|"hyphenator"
argument_list|,
literal|"da_UTF8.xml"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"minSubwordSize"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"maxSubwordSize"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setLuceneMatchVersion
argument_list|(
name|TEST_VERSION_CURRENT
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
name|loader
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
literal|"basketballkurv"
block|,
literal|"ba"
block|,
literal|"sket"
block|,
literal|"bal"
block|,
literal|"ball"
block|,
literal|"kurv"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
