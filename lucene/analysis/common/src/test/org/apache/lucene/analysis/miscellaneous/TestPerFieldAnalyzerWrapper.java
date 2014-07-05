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
name|Collections
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
name|AnalyzerWrapper
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
name|MockCharFilter
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
name|TokenFilter
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
name|core
operator|.
name|SimpleAnalyzer
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
name|WhitespaceAnalyzer
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
name|CharTermAttribute
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
name|Rethrow
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|TestPerFieldAnalyzerWrapper
specifier|public
class|class
name|TestPerFieldAnalyzerWrapper
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testPerField
specifier|public
name|void
name|testPerField
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|text
init|=
literal|"Qwerty"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Analyzer
argument_list|>
name|analyzerPerField
init|=
name|Collections
operator|.
expr|<
name|String
decl_stmt|,
name|Analyzer
decl|>
name|singletonMap
argument_list|(
literal|"special"
argument_list|,
operator|new
name|SimpleAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|)
decl_stmt|;
name|PerFieldAnalyzerWrapper
name|analyzer
init|=
operator|new
name|PerFieldAnalyzerWrapper
argument_list|(
operator|new
name|WhitespaceAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|,
name|analyzerPerField
argument_list|)
decl_stmt|;
try|try
init|(
name|TokenStream
name|tokenStream
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|"field"
argument_list|,
name|text
argument_list|)
init|)
block|{
name|CharTermAttribute
name|termAtt
init|=
name|tokenStream
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|tokenStream
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|tokenStream
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"WhitespaceAnalyzer does not lowercase"
argument_list|,
literal|"Qwerty"
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tokenStream
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|tokenStream
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
try|try
init|(
name|TokenStream
name|tokenStream
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|"special"
argument_list|,
name|text
argument_list|)
init|)
block|{
name|CharTermAttribute
name|termAtt
init|=
name|tokenStream
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|tokenStream
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|tokenStream
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"SimpleAnalyzer lowercases"
argument_list|,
literal|"qwerty"
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tokenStream
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|tokenStream
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testReuseWrapped
specifier|public
name|void
name|testReuseWrapped
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|text
init|=
literal|"Qwerty"
decl_stmt|;
specifier|final
name|Analyzer
name|specialAnalyzer
init|=
operator|new
name|SimpleAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
specifier|final
name|Analyzer
name|defaultAnalyzer
init|=
operator|new
name|WhitespaceAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
name|TokenStream
name|ts1
decl_stmt|,
name|ts2
decl_stmt|,
name|ts3
decl_stmt|,
name|ts4
decl_stmt|;
specifier|final
name|PerFieldAnalyzerWrapper
name|wrapper1
init|=
operator|new
name|PerFieldAnalyzerWrapper
argument_list|(
name|defaultAnalyzer
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Analyzer
operator|>
name|singletonMap
argument_list|(
literal|"special"
argument_list|,
name|specialAnalyzer
argument_list|)
argument_list|)
decl_stmt|;
comment|// test that the PerFieldWrapper returns the same instance as original Analyzer:
name|ts1
operator|=
name|defaultAnalyzer
operator|.
name|tokenStream
argument_list|(
literal|"something"
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|ts2
operator|=
name|wrapper1
operator|.
name|tokenStream
argument_list|(
literal|"something"
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|ts1
argument_list|,
name|ts2
argument_list|)
expr_stmt|;
name|ts1
operator|=
name|specialAnalyzer
operator|.
name|tokenStream
argument_list|(
literal|"special"
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|ts2
operator|=
name|wrapper1
operator|.
name|tokenStream
argument_list|(
literal|"special"
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|ts1
argument_list|,
name|ts2
argument_list|)
expr_stmt|;
comment|// Wrap with another wrapper, which does *not* extend DelegatingAnalyzerWrapper:
specifier|final
name|AnalyzerWrapper
name|wrapper2
init|=
operator|new
name|AnalyzerWrapper
argument_list|(
name|wrapper1
operator|.
name|getReuseStrategy
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Analyzer
name|getWrappedAnalyzer
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|wrapper1
return|;
block|}
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|wrapComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|TokenStreamComponents
name|components
parameter_list|)
block|{
try|try
block|{
name|assertNotSame
argument_list|(
name|specialAnalyzer
operator|.
name|tokenStream
argument_list|(
literal|"special"
argument_list|,
name|text
argument_list|)
argument_list|,
name|components
operator|.
name|getTokenStream
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
name|Rethrow
operator|.
name|rethrow
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|TokenFilter
name|filter
init|=
operator|new
name|ASCIIFoldingFilter
argument_list|(
name|components
operator|.
name|getTokenStream
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|components
operator|.
name|getTokenizer
argument_list|()
argument_list|,
name|filter
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|ts3
operator|=
name|wrapper2
operator|.
name|tokenStream
argument_list|(
literal|"special"
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|ts1
argument_list|,
name|ts3
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ts3
operator|instanceof
name|ASCIIFoldingFilter
argument_list|)
expr_stmt|;
comment|// check that cache did not get corrumpted:
name|ts2
operator|=
name|wrapper1
operator|.
name|tokenStream
argument_list|(
literal|"special"
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|ts1
argument_list|,
name|ts2
argument_list|)
expr_stmt|;
comment|// Wrap PerField with another PerField. In that case all TokenStreams returned must be the same:
specifier|final
name|PerFieldAnalyzerWrapper
name|wrapper3
init|=
operator|new
name|PerFieldAnalyzerWrapper
argument_list|(
name|wrapper1
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Analyzer
operator|>
name|singletonMap
argument_list|(
literal|"moreSpecial"
argument_list|,
name|specialAnalyzer
argument_list|)
argument_list|)
decl_stmt|;
name|ts1
operator|=
name|specialAnalyzer
operator|.
name|tokenStream
argument_list|(
literal|"special"
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|ts2
operator|=
name|wrapper3
operator|.
name|tokenStream
argument_list|(
literal|"special"
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|ts1
argument_list|,
name|ts2
argument_list|)
expr_stmt|;
name|ts3
operator|=
name|specialAnalyzer
operator|.
name|tokenStream
argument_list|(
literal|"moreSpecial"
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|ts4
operator|=
name|wrapper3
operator|.
name|tokenStream
argument_list|(
literal|"moreSpecial"
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|ts3
argument_list|,
name|ts4
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|ts2
argument_list|,
name|ts3
argument_list|)
expr_stmt|;
block|}
DECL|method|testCharFilters
specifier|public
name|void
name|testCharFilters
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
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
return|return
operator|new
name|TokenStreamComponents
argument_list|(
operator|new
name|MockTokenizer
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Reader
name|initReader
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|MockCharFilter
argument_list|(
name|reader
argument_list|,
literal|7
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ab"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aab"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|}
argument_list|)
expr_stmt|;
comment|// now wrap in PFAW
name|PerFieldAnalyzerWrapper
name|p
init|=
operator|new
name|PerFieldAnalyzerWrapper
argument_list|(
name|a
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Analyzer
operator|>
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|p
argument_list|,
literal|"ab"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aab"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
