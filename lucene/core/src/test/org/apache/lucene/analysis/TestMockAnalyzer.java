begin_unit
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package
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
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|_TestUtil
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
name|automaton
operator|.
name|Automaton
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
name|automaton
operator|.
name|BasicAutomata
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
name|automaton
operator|.
name|BasicOperations
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
name|automaton
operator|.
name|CharacterRunAutomaton
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
name|automaton
operator|.
name|RegExp
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|TestMockAnalyzer
specifier|public
class|class
name|TestMockAnalyzer
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/** Test a configuration that behaves a lot like WhitespaceAnalyzer */
DECL|method|testWhitespace
specifier|public
name|void
name|testWhitespace
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"A bc defg hiJklmn opqrstuv wxy z "
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"bc"
block|,
literal|"defg"
block|,
literal|"hijklmn"
block|,
literal|"opqrstuv"
block|,
literal|"wxy"
block|,
literal|"z"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"aba cadaba shazam"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aba"
block|,
literal|"cadaba"
block|,
literal|"shazam"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"break on whitespace"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"break"
block|,
literal|"on"
block|,
literal|"whitespace"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Test a configuration that behaves a lot like SimpleAnalyzer */
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|SIMPLE
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"a-bc123 defg+hijklmn567opqrstuv78wxy_z "
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"bc"
block|,
literal|"defg"
block|,
literal|"hijklmn"
block|,
literal|"opqrstuv"
block|,
literal|"wxy"
block|,
literal|"z"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"aba4cadaba-Shazam"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aba"
block|,
literal|"cadaba"
block|,
literal|"shazam"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"break+on/Letters"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"break"
block|,
literal|"on"
block|,
literal|"letters"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Test a configuration that behaves a lot like KeywordAnalyzer */
DECL|method|testKeyword
specifier|public
name|void
name|testKeyword
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
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
literal|false
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"a-bc123 defg+hijklmn567opqrstuv78wxy_z "
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a-bc123 defg+hijklmn567opqrstuv78wxy_z "
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"aba4cadaba-Shazam"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aba4cadaba-Shazam"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"break+on/Nothing"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"break+on/Nothing"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Test a configuration that behaves a lot like StopAnalyzer */
DECL|method|testStop
specifier|public
name|void
name|testStop
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|SIMPLE
argument_list|,
literal|true
argument_list|,
name|MockTokenFilter
operator|.
name|ENGLISH_STOPSET
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"the quick brown a fox"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"quick"
block|,
literal|"brown"
block|,
literal|"fox"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|1
block|,
literal|2
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Test a configuration that behaves a lot like KeepWordFilter */
DECL|method|testKeep
specifier|public
name|void
name|testKeep
parameter_list|()
throws|throws
name|Exception
block|{
name|CharacterRunAutomaton
name|keepWords
init|=
operator|new
name|CharacterRunAutomaton
argument_list|(
name|BasicOperations
operator|.
name|complement
argument_list|(
name|Automaton
operator|.
name|union
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|BasicAutomata
operator|.
name|makeString
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|BasicAutomata
operator|.
name|makeString
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|SIMPLE
argument_list|,
literal|true
argument_list|,
name|keepWords
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"quick foo brown bar bar fox foo"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"bar"
block|,
literal|"bar"
block|,
literal|"foo"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|2
block|,
literal|1
block|,
literal|2
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Test a configuration that behaves a lot like LengthFilter */
DECL|method|testLength
specifier|public
name|void
name|testLength
parameter_list|()
throws|throws
name|Exception
block|{
name|CharacterRunAutomaton
name|length5
init|=
operator|new
name|CharacterRunAutomaton
argument_list|(
operator|new
name|RegExp
argument_list|(
literal|".{5,}"
argument_list|)
operator|.
name|toAutomaton
argument_list|()
argument_list|)
decl_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|true
argument_list|,
name|length5
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ok toolong fine notfine"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ok"
block|,
literal|"fine"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testLUCENE_3042
specifier|public
name|void
name|testLUCENE_3042
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|testString
init|=
literal|"t"
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|"dummy"
argument_list|,
name|testString
argument_list|)
decl_stmt|;
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|stream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
comment|// consume
block|}
name|stream
operator|.
name|end
argument_list|()
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
name|testString
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"t"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** blast some random strings through the analyzer */
DECL|method|testRandomStrings
specifier|public
name|void
name|testRandomStrings
parameter_list|()
throws|throws
name|Exception
block|{
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
name|atLeast
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testForwardOffsets
specifier|public
name|void
name|testForwardOffsets
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|num
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
init|=
name|_TestUtil
operator|.
name|randomHtmlishString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|MockCharFilter
name|charfilter
init|=
operator|new
name|MockCharFilter
argument_list|(
name|reader
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|MockAnalyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|"bogus"
argument_list|,
name|charfilter
argument_list|)
decl_stmt|;
name|ts
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|ts
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
empty_stmt|;
block|}
name|ts
operator|.
name|end
argument_list|()
expr_stmt|;
name|ts
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testWrapReader
specifier|public
name|void
name|testWrapReader
parameter_list|()
throws|throws
name|Exception
block|{
comment|// LUCENE-5153: test that wrapping an analyzer's reader is allowed
specifier|final
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
specifier|final
name|Analyzer
name|delegate
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|AnalyzerWrapper
argument_list|(
name|delegate
operator|.
name|getReuseStrategy
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Reader
name|wrapReader
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
return|return
name|components
return|;
block|}
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
name|delegate
return|;
block|}
block|}
decl_stmt|;
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"abc"
argument_list|,
literal|"aabc"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
