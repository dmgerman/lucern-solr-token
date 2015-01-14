begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.custom
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|custom
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|List
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
name|charfilter
operator|.
name|HTMLStripCharFilterFactory
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
name|LowerCaseFilterFactory
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
name|StopFilterFactory
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
name|WhitespaceTokenizerFactory
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
name|ASCIIFoldingFilterFactory
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
name|standard
operator|.
name|ClassicTokenizerFactory
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
name|CharFilterFactory
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
name|TokenFilterFactory
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
name|SetOnce
operator|.
name|AlreadySetException
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
begin_class
DECL|class|TestCustomAnalyzer
specifier|public
class|class
name|TestCustomAnalyzer
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|// Test some examples (TODO: we only check behavior, we may need something like TestRandomChains...)
DECL|method|testWhitespaceWithFolding
specifier|public
name|void
name|testWhitespaceWithFolding
parameter_list|()
throws|throws
name|Exception
block|{
name|CustomAnalyzer
name|a
init|=
name|CustomAnalyzer
operator|.
name|builder
argument_list|()
operator|.
name|withTokenizer
argument_list|(
literal|"whitespace"
argument_list|)
operator|.
name|addTokenFilter
argument_list|(
literal|"asciifolding"
argument_list|,
literal|"preserveOriginal"
argument_list|,
literal|"true"
argument_list|)
operator|.
name|addTokenFilter
argument_list|(
literal|"lowercase"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|WhitespaceTokenizerFactory
operator|.
name|class
argument_list|,
name|a
operator|.
name|getTokenizerFactory
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|,
name|a
operator|.
name|getCharFilterFactories
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|TokenFilterFactory
argument_list|>
name|tokenFilters
init|=
name|a
operator|.
name|getTokenFilterFactories
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|tokenFilters
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|ASCIIFoldingFilterFactory
operator|.
name|class
argument_list|,
name|tokenFilters
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|LowerCaseFilterFactory
operator|.
name|class
argument_list|,
name|tokenFilters
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|a
operator|.
name|getPositionIncrementGap
argument_list|(
literal|"dummy"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|a
operator|.
name|getOffsetGap
argument_list|(
literal|"dummy"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|Version
operator|.
name|LATEST
argument_list|,
name|a
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"foo bar FOO BAR"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"bar"
block|,
literal|"foo"
block|,
literal|"bar"
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
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"fÃ¶Ã³ bÃ¤r FÃÃ BAR"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"fÃ¶Ã³"
block|,
literal|"bar"
block|,
literal|"bÃ¤r"
block|,
literal|"foo"
block|,
literal|"fÃ¶Ã¶"
block|,
literal|"bar"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testHtmlStripClassicFolding
specifier|public
name|void
name|testHtmlStripClassicFolding
parameter_list|()
throws|throws
name|Exception
block|{
name|CustomAnalyzer
name|a
init|=
name|CustomAnalyzer
operator|.
name|builder
argument_list|()
operator|.
name|withDefaultMatchVersion
argument_list|(
name|Version
operator|.
name|LUCENE_5_0_0
argument_list|)
operator|.
name|addCharFilter
argument_list|(
literal|"htmlstrip"
argument_list|)
operator|.
name|withTokenizer
argument_list|(
literal|"classic"
argument_list|)
operator|.
name|addTokenFilter
argument_list|(
literal|"asciifolding"
argument_list|,
literal|"preserveOriginal"
argument_list|,
literal|"true"
argument_list|)
operator|.
name|addTokenFilter
argument_list|(
literal|"lowercase"
argument_list|)
operator|.
name|withPositionIncrementGap
argument_list|(
literal|100
argument_list|)
operator|.
name|withOffsetGap
argument_list|(
literal|1000
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|ClassicTokenizerFactory
operator|.
name|class
argument_list|,
name|a
operator|.
name|getTokenizerFactory
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|CharFilterFactory
argument_list|>
name|charFilters
init|=
name|a
operator|.
name|getCharFilterFactories
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|charFilters
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HTMLStripCharFilterFactory
operator|.
name|class
argument_list|,
name|charFilters
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|TokenFilterFactory
argument_list|>
name|tokenFilters
init|=
name|a
operator|.
name|getTokenFilterFactories
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|tokenFilters
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|ASCIIFoldingFilterFactory
operator|.
name|class
argument_list|,
name|tokenFilters
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|LowerCaseFilterFactory
operator|.
name|class
argument_list|,
name|tokenFilters
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|a
operator|.
name|getPositionIncrementGap
argument_list|(
literal|"dummy"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1000
argument_list|,
name|a
operator|.
name|getOffsetGap
argument_list|(
literal|"dummy"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|Version
operator|.
name|LUCENE_5_0_0
argument_list|,
name|a
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"<p>foo bar</p> FOO BAR"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"bar"
block|,
literal|"foo"
block|,
literal|"bar"
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
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"<p><b>fÃ¶Ã³</b> bÃ¤r     FÃÃ BAR</p>"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"fÃ¶Ã³"
block|,
literal|"bar"
block|,
literal|"bÃ¤r"
block|,
literal|"foo"
block|,
literal|"fÃ¶Ã¶"
block|,
literal|"bar"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testStopWordsFromClasspath
specifier|public
name|void
name|testStopWordsFromClasspath
parameter_list|()
throws|throws
name|Exception
block|{
name|CustomAnalyzer
name|a
init|=
name|CustomAnalyzer
operator|.
name|builder
argument_list|()
operator|.
name|withTokenizer
argument_list|(
literal|"whitespace"
argument_list|)
operator|.
name|addTokenFilter
argument_list|(
literal|"stop"
argument_list|,
literal|"ignoreCase"
argument_list|,
literal|"true"
argument_list|,
literal|"words"
argument_list|,
literal|"org/apache/lucene/analysis/custom/teststop.txt"
argument_list|,
literal|"format"
argument_list|,
literal|"wordset"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|WhitespaceTokenizerFactory
operator|.
name|class
argument_list|,
name|a
operator|.
name|getTokenizerFactory
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|,
name|a
operator|.
name|getCharFilterFactories
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|TokenFilterFactory
argument_list|>
name|tokenFilters
init|=
name|a
operator|.
name|getTokenFilterFactories
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tokenFilters
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|StopFilterFactory
operator|.
name|class
argument_list|,
name|tokenFilters
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|a
operator|.
name|getPositionIncrementGap
argument_list|(
literal|"dummy"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|a
operator|.
name|getOffsetGap
argument_list|(
literal|"dummy"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|Version
operator|.
name|LATEST
argument_list|,
name|a
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"foo Foo Bar"
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|testStopWordsFromClasspathWithMap
specifier|public
name|void
name|testStopWordsFromClasspathWithMap
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|stopConfig1
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|stopConfig1
operator|.
name|put
argument_list|(
literal|"ignoreCase"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|stopConfig1
operator|.
name|put
argument_list|(
literal|"words"
argument_list|,
literal|"org/apache/lucene/analysis/custom/teststop.txt"
argument_list|)
expr_stmt|;
name|stopConfig1
operator|.
name|put
argument_list|(
literal|"format"
argument_list|,
literal|"wordset"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|stopConfig2
init|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|stopConfig1
argument_list|)
argument_list|)
decl_stmt|;
name|CustomAnalyzer
name|a
init|=
name|CustomAnalyzer
operator|.
name|builder
argument_list|()
operator|.
name|withTokenizer
argument_list|(
literal|"whitespace"
argument_list|)
operator|.
name|addTokenFilter
argument_list|(
literal|"stop"
argument_list|,
name|stopConfig1
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|stopConfig1
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"foo Foo Bar"
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
comment|// try with unmodifiableMap, should fail
try|try
block|{
name|CustomAnalyzer
operator|.
name|builder
argument_list|()
operator|.
name|withTokenizer
argument_list|(
literal|"whitespace"
argument_list|)
operator|.
name|addTokenFilter
argument_list|(
literal|"stop"
argument_list|,
name|stopConfig2
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
decl||
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
comment|// pass
block|}
block|}
DECL|method|testStopWordsFromFile
specifier|public
name|void
name|testStopWordsFromFile
parameter_list|()
throws|throws
name|Exception
block|{
name|CustomAnalyzer
name|a
init|=
name|CustomAnalyzer
operator|.
name|builder
argument_list|(
name|this
operator|.
name|getDataPath
argument_list|(
literal|""
argument_list|)
argument_list|)
operator|.
name|withTokenizer
argument_list|(
literal|"whitespace"
argument_list|)
operator|.
name|addTokenFilter
argument_list|(
literal|"stop"
argument_list|,
literal|"ignoreCase"
argument_list|,
literal|"true"
argument_list|,
literal|"words"
argument_list|,
literal|"teststop.txt"
argument_list|,
literal|"format"
argument_list|,
literal|"wordset"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"foo Foo Bar"
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|testStopWordsFromFileAbsolute
specifier|public
name|void
name|testStopWordsFromFileAbsolute
parameter_list|()
throws|throws
name|Exception
block|{
name|CustomAnalyzer
name|a
init|=
name|CustomAnalyzer
operator|.
name|builder
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"."
argument_list|)
argument_list|)
operator|.
name|withTokenizer
argument_list|(
literal|"whitespace"
argument_list|)
operator|.
name|addTokenFilter
argument_list|(
literal|"stop"
argument_list|,
literal|"ignoreCase"
argument_list|,
literal|"true"
argument_list|,
literal|"words"
argument_list|,
name|this
operator|.
name|getDataPath
argument_list|(
literal|"teststop.txt"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
literal|"format"
argument_list|,
literal|"wordset"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"foo Foo Bar"
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
comment|// Now test misconfigurations:
DECL|method|testIncorrectOrder
specifier|public
name|void
name|testIncorrectOrder
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|CustomAnalyzer
operator|.
name|builder
argument_list|()
operator|.
name|addCharFilter
argument_list|(
literal|"htmlstrip"
argument_list|)
operator|.
name|withDefaultMatchVersion
argument_list|(
name|Version
operator|.
name|LATEST
argument_list|)
operator|.
name|withTokenizer
argument_list|(
literal|"whitespace"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// pass
block|}
block|}
DECL|method|testMissingSPI
specifier|public
name|void
name|testMissingSPI
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|CustomAnalyzer
operator|.
name|builder
argument_list|()
operator|.
name|withTokenizer
argument_list|(
literal|"foobar_nonexistent"
argument_list|)
operator|.
name|build
argument_list|()
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
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"SPI"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"does not exist"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSetTokenizerTwice
specifier|public
name|void
name|testSetTokenizerTwice
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|CustomAnalyzer
operator|.
name|builder
argument_list|()
operator|.
name|withTokenizer
argument_list|(
literal|"whitespace"
argument_list|)
operator|.
name|withTokenizer
argument_list|(
literal|"standard"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadySetException
name|e
parameter_list|)
block|{
comment|// pass
block|}
block|}
DECL|method|testSetMatchVersionTwice
specifier|public
name|void
name|testSetMatchVersionTwice
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|CustomAnalyzer
operator|.
name|builder
argument_list|()
operator|.
name|withDefaultMatchVersion
argument_list|(
name|Version
operator|.
name|LATEST
argument_list|)
operator|.
name|withDefaultMatchVersion
argument_list|(
name|Version
operator|.
name|LATEST
argument_list|)
operator|.
name|withTokenizer
argument_list|(
literal|"standard"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadySetException
name|e
parameter_list|)
block|{
comment|// pass
block|}
block|}
DECL|method|testSetPosIncTwice
specifier|public
name|void
name|testSetPosIncTwice
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|CustomAnalyzer
operator|.
name|builder
argument_list|()
operator|.
name|withPositionIncrementGap
argument_list|(
literal|2
argument_list|)
operator|.
name|withPositionIncrementGap
argument_list|(
literal|3
argument_list|)
operator|.
name|withTokenizer
argument_list|(
literal|"standard"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadySetException
name|e
parameter_list|)
block|{
comment|// pass
block|}
block|}
DECL|method|testSetOfsGapTwice
specifier|public
name|void
name|testSetOfsGapTwice
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|CustomAnalyzer
operator|.
name|builder
argument_list|()
operator|.
name|withOffsetGap
argument_list|(
literal|2
argument_list|)
operator|.
name|withOffsetGap
argument_list|(
literal|3
argument_list|)
operator|.
name|withTokenizer
argument_list|(
literal|"standard"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadySetException
name|e
parameter_list|)
block|{
comment|// pass
block|}
block|}
DECL|method|testNoTokenizer
specifier|public
name|void
name|testNoTokenizer
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|CustomAnalyzer
operator|.
name|builder
argument_list|()
operator|.
name|build
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|equals
argument_list|(
literal|"You have to set at least a tokenizer."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testNullTokenizer
specifier|public
name|void
name|testNullTokenizer
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|CustomAnalyzer
operator|.
name|builder
argument_list|()
operator|.
name|withTokenizer
argument_list|(
literal|null
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
comment|// pass
block|}
block|}
DECL|method|testNullParamKey
specifier|public
name|void
name|testNullParamKey
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|CustomAnalyzer
operator|.
name|builder
argument_list|()
operator|.
name|withTokenizer
argument_list|(
literal|"whitespace"
argument_list|,
literal|null
argument_list|,
literal|"foo"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
comment|// pass
block|}
block|}
DECL|method|testNullMatchVersion
specifier|public
name|void
name|testNullMatchVersion
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|CustomAnalyzer
operator|.
name|builder
argument_list|()
operator|.
name|withDefaultMatchVersion
argument_list|(
literal|null
argument_list|)
operator|.
name|withTokenizer
argument_list|(
literal|"whitespace"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
comment|// pass
block|}
block|}
block|}
end_class
end_unit
