begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.ru
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ru
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
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import
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
name|InputStreamReader
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
name|CharArraySet
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
name|tokenattributes
operator|.
name|TermAttribute
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
name|util
operator|.
name|Version
import|;
end_import
begin_comment
comment|/**  * Test case for RussianAnalyzer.  */
end_comment
begin_class
DECL|class|TestRussianAnalyzer
specifier|public
class|class
name|TestRussianAnalyzer
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|inWords
specifier|private
name|InputStreamReader
name|inWords
decl_stmt|;
DECL|field|sampleUnicode
specifier|private
name|InputStreamReader
name|sampleUnicode
decl_stmt|;
DECL|field|dataDir
specifier|private
name|File
name|dataDir
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|dataDir
operator|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"dataDir"
argument_list|,
literal|"./bin"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * @deprecated remove this test and its datafiles in Lucene 4.0      * the Snowball version has its own data tests.      */
annotation|@
name|Deprecated
DECL|method|testUnicode30
specifier|public
name|void
name|testUnicode30
parameter_list|()
throws|throws
name|IOException
block|{
name|RussianAnalyzer
name|ra
init|=
operator|new
name|RussianAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|)
decl_stmt|;
name|inWords
operator|=
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"/org/apache/lucene/analysis/ru/testUTF8.txt"
argument_list|)
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|sampleUnicode
operator|=
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"/org/apache/lucene/analysis/ru/resUTF8.htm"
argument_list|)
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|TokenStream
name|in
init|=
name|ra
operator|.
name|tokenStream
argument_list|(
literal|"all"
argument_list|,
name|inWords
argument_list|)
decl_stmt|;
name|RussianLetterTokenizer
name|sample
init|=
operator|new
name|RussianLetterTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|sampleUnicode
argument_list|)
decl_stmt|;
name|TermAttribute
name|text
init|=
name|in
operator|.
name|getAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|TermAttribute
name|sampleText
init|=
name|sample
operator|.
name|getAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|in
operator|.
name|incrementToken
argument_list|()
operator|==
literal|false
condition|)
break|break;
name|boolean
name|nextSampleToken
init|=
name|sample
operator|.
name|incrementToken
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Unicode"
argument_list|,
name|text
operator|.
name|term
argument_list|()
argument_list|,
name|nextSampleToken
operator|==
literal|false
condition|?
literal|null
else|:
name|sampleText
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|inWords
operator|.
name|close
argument_list|()
expr_stmt|;
name|sampleUnicode
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testDigitsInRussianCharset
specifier|public
name|void
name|testDigitsInRussianCharset
parameter_list|()
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"text 1000"
argument_list|)
decl_stmt|;
name|RussianAnalyzer
name|ra
init|=
operator|new
name|RussianAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
name|ra
operator|.
name|tokenStream
argument_list|(
literal|""
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|TermAttribute
name|termText
init|=
name|stream
operator|.
name|getAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
name|assertTrue
argument_list|(
name|stream
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"text"
argument_list|,
name|termText
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stream
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"RussianAnalyzer's tokenizer skips numbers from input text"
argument_list|,
literal|"1000"
argument_list|,
name|termText
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|stream
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"unexpected IOException"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** @deprecated remove this test in Lucene 4.0: stopwords changed */
annotation|@
name|Deprecated
DECL|method|testReusableTokenStream30
specifier|public
name|void
name|testReusableTokenStream30
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|RussianAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|)
decl_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"ÐÐ¼ÐµÑÑÐµ Ñ ÑÐµÐ¼ Ð¾ ÑÐ¸Ð»Ðµ ÑÐ»ÐµÐºÑÑÐ¾Ð¼Ð°Ð³Ð½Ð¸ÑÐ½Ð¾Ð¹ ÑÐ½ÐµÑÐ³Ð¸Ð¸ Ð¸Ð¼ÐµÐ»Ð¸ Ð¿ÑÐµÐ´ÑÑÐ°Ð²Ð»ÐµÐ½Ð¸Ðµ ÐµÑÐµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð²Ð¼ÐµÑÑ"
block|,
literal|"ÑÐ¸Ð»"
block|,
literal|"ÑÐ»ÐµÐºÑÑÐ¾Ð¼Ð°Ð³Ð½Ð¸ÑÐ½"
block|,
literal|"ÑÐ½ÐµÑÐ³"
block|,
literal|"Ð¸Ð¼ÐµÐ»"
block|,
literal|"Ð¿ÑÐµÐ´ÑÑÐ°Ð²Ð»ÐµÐ½"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"ÐÐ¾ Ð·Ð½Ð°Ð½Ð¸Ðµ ÑÑÐ¾ ÑÑÐ°Ð½Ð¸Ð»Ð¾ÑÑ Ð² ÑÐ°Ð¹Ð½Ðµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð·Ð½Ð°Ð½"
block|,
literal|"ÑÑÐ°Ð½"
block|,
literal|"ÑÐ°Ð¹Ð½"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testReusableTokenStream
specifier|public
name|void
name|testReusableTokenStream
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|RussianAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"ÐÐ¼ÐµÑÑÐµ Ñ ÑÐµÐ¼ Ð¾ ÑÐ¸Ð»Ðµ ÑÐ»ÐµÐºÑÑÐ¾Ð¼Ð°Ð³Ð½Ð¸ÑÐ½Ð¾Ð¹ ÑÐ½ÐµÑÐ³Ð¸Ð¸ Ð¸Ð¼ÐµÐ»Ð¸ Ð¿ÑÐµÐ´ÑÑÐ°Ð²Ð»ÐµÐ½Ð¸Ðµ ÐµÑÐµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð²Ð¼ÐµÑÑ"
block|,
literal|"ÑÐ¸Ð»"
block|,
literal|"ÑÐ»ÐµÐºÑÑÐ¾Ð¼Ð°Ð³Ð½Ð¸ÑÐ½"
block|,
literal|"ÑÐ½ÐµÑÐ³"
block|,
literal|"Ð¸Ð¼ÐµÐ»"
block|,
literal|"Ð¿ÑÐµÐ´ÑÑÐ°Ð²Ð»ÐµÐ½"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"ÐÐ¾ Ð·Ð½Ð°Ð½Ð¸Ðµ ÑÑÐ¾ ÑÑÐ°Ð½Ð¸Ð»Ð¾ÑÑ Ð² ÑÐ°Ð¹Ð½Ðµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð·Ð½Ð°Ð½"
block|,
literal|"ÑÑ"
block|,
literal|"ÑÑÐ°Ð½"
block|,
literal|"ÑÐ°Ð¹Ð½"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testWithStemExclusionSet
specifier|public
name|void
name|testWithStemExclusionSet
parameter_list|()
throws|throws
name|Exception
block|{
name|CharArraySet
name|set
init|=
operator|new
name|CharArraySet
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
literal|"Ð¿ÑÐµÐ´ÑÑÐ°Ð²Ð»ÐµÐ½Ð¸Ðµ"
argument_list|)
expr_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|RussianAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|RussianAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|,
name|set
argument_list|)
decl_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"ÐÐ¼ÐµÑÑÐµ Ñ ÑÐµÐ¼ Ð¾ ÑÐ¸Ð»Ðµ ÑÐ»ÐµÐºÑÑÐ¾Ð¼Ð°Ð³Ð½Ð¸ÑÐ½Ð¾Ð¹ ÑÐ½ÐµÑÐ³Ð¸Ð¸ Ð¸Ð¼ÐµÐ»Ð¸ Ð¿ÑÐµÐ´ÑÑÐ°Ð²Ð»ÐµÐ½Ð¸Ðµ ÐµÑÐµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð²Ð¼ÐµÑÑ"
block|,
literal|"ÑÐ¸Ð»"
block|,
literal|"ÑÐ»ÐµÐºÑÑÐ¾Ð¼Ð°Ð³Ð½Ð¸ÑÐ½"
block|,
literal|"ÑÐ½ÐµÑÐ³"
block|,
literal|"Ð¸Ð¼ÐµÐ»"
block|,
literal|"Ð¿ÑÐµÐ´ÑÑÐ°Ð²Ð»ÐµÐ½Ð¸Ðµ"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
