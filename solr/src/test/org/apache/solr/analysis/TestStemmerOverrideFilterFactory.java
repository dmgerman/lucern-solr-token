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
name|core
operator|.
name|WhitespaceTokenizer
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
name|en
operator|.
name|PorterStemFilter
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
name|solr
operator|.
name|common
operator|.
name|ResourceLoader
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
comment|/**  * Simple tests to ensure the stemmer override filter factory is working.  */
end_comment
begin_class
DECL|class|TestStemmerOverrideFilterFactory
specifier|public
class|class
name|TestStemmerOverrideFilterFactory
extends|extends
name|BaseTokenTestCase
block|{
DECL|method|testKeywords
specifier|public
name|void
name|testKeywords
parameter_list|()
throws|throws
name|IOException
block|{
comment|// our stemdict stems dogs to 'cat'
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"testing dogs"
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
name|StemmerOverrideFilterFactory
name|factory
init|=
operator|new
name|StemmerOverrideFilterFactory
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
argument_list|(
name|DEFAULT_VERSION_PARAM
argument_list|)
decl_stmt|;
name|ResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"dictionary"
argument_list|,
literal|"stemdict.txt"
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
name|ts
init|=
operator|new
name|PorterStemFilter
argument_list|(
name|factory
operator|.
name|create
argument_list|(
name|tokenizer
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
literal|"test"
block|,
literal|"cat"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testKeywordsCaseInsensitive
specifier|public
name|void
name|testKeywordsCaseInsensitive
parameter_list|()
throws|throws
name|IOException
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"testing DoGs"
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
name|StemmerOverrideFilterFactory
name|factory
init|=
operator|new
name|StemmerOverrideFilterFactory
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
argument_list|(
name|DEFAULT_VERSION_PARAM
argument_list|)
decl_stmt|;
name|ResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"dictionary"
argument_list|,
literal|"stemdict.txt"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"ignoreCase"
argument_list|,
literal|"true"
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
name|ts
init|=
operator|new
name|PorterStemFilter
argument_list|(
name|factory
operator|.
name|create
argument_list|(
name|tokenizer
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
literal|"test"
block|,
literal|"cat"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
