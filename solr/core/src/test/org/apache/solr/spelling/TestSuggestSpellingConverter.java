begin_unit
begin_package
DECL|package|org.apache.solr.spelling
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
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
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|CannedTokenStream
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
name|MockAnalyzer
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
name|Token
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
name|KeywordTokenizer
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
name|LowerCaseFilter
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
name|miscellaneous
operator|.
name|TrimFilter
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
name|pattern
operator|.
name|PatternReplaceFilter
import|;
end_import
begin_class
DECL|class|TestSuggestSpellingConverter
specifier|public
class|class
name|TestSuggestSpellingConverter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|converter
name|SuggestQueryConverter
name|converter
init|=
operator|new
name|SuggestQueryConverter
argument_list|()
decl_stmt|;
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
comment|// lowercases only!
name|converter
operator|.
name|setAnalyzer
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertConvertsTo
argument_list|(
literal|"This is a test"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"this is a test"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testComplicated
specifier|public
name|void
name|testComplicated
parameter_list|()
throws|throws
name|Exception
block|{
comment|// lowercases, removes field names, other syntax, collapses runs of whitespace, etc.
name|converter
operator|.
name|setAnalyzer
argument_list|(
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
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|KeywordTokenizer
argument_list|()
decl_stmt|;
name|TokenStream
name|filter
init|=
operator|new
name|PatternReplaceFilter
argument_list|(
name|tokenizer
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
literal|"([^\\p{L}\\p{M}\\p{N}\\p{Cs}]*[\\p{L}\\p{M}\\p{N}\\p{Cs}\\_]+:)|([^\\p{L}\\p{M}\\p{N}\\p{Cs}])+"
argument_list|)
argument_list|,
literal|" "
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|filter
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|filter
argument_list|)
expr_stmt|;
name|filter
operator|=
operator|new
name|TrimFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|filter
argument_list|)
expr_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|filter
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertConvertsTo
argument_list|(
literal|"test1 +test2"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"test1 test2"
block|}
argument_list|)
expr_stmt|;
name|assertConvertsTo
argument_list|(
literal|"test~"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"test"
block|}
argument_list|)
expr_stmt|;
name|assertConvertsTo
argument_list|(
literal|"field:test"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"test"
block|}
argument_list|)
expr_stmt|;
name|assertConvertsTo
argument_list|(
literal|"This is a test"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"this is a test"
block|}
argument_list|)
expr_stmt|;
name|assertConvertsTo
argument_list|(
literal|" This is  a test"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"this is a test"
block|}
argument_list|)
expr_stmt|;
name|assertConvertsTo
argument_list|(
literal|"Foo (field:bar) text_hi:à¤¹à¤¿à¤¨à¥à¤¦à¥    "
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo bar à¤¹à¤¿à¤¨à¥à¤¦à¥"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|assertConvertsTo
specifier|public
name|void
name|assertConvertsTo
parameter_list|(
name|String
name|text
parameter_list|,
name|String
name|expected
index|[]
parameter_list|)
throws|throws
name|IOException
block|{
name|Collection
argument_list|<
name|Token
argument_list|>
name|tokens
init|=
name|converter
operator|.
name|convert
argument_list|(
name|text
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
operator|new
name|CannedTokenStream
argument_list|(
name|tokens
operator|.
name|toArray
argument_list|(
operator|new
name|Token
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
