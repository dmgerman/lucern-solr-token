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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|StandardAnalyzer
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
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *<p/>  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|TestStandardAnalyzer
specifier|public
class|class
name|TestStandardAnalyzer
extends|extends
name|TestCase
block|{
DECL|method|assertAnalyzesTo
specifier|public
name|void
name|assertAnalyzesTo
parameter_list|(
name|Analyzer
name|a
parameter_list|,
name|String
name|input
parameter_list|,
name|String
index|[]
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
name|TokenStream
name|ts
init|=
name|a
operator|.
name|tokenStream
argument_list|(
literal|"dummy"
argument_list|,
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
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
name|expected
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Token
name|t
init|=
name|ts
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
index|[
name|i
index|]
argument_list|,
name|t
operator|.
name|termText
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|ts
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|ts
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testStandard
specifier|public
name|void
name|testStandard
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|StandardAnalyzer
argument_list|()
decl_stmt|;
comment|// alphanumeric tokens
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"B2B"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"b2b"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"2B"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"2b"
block|}
argument_list|)
expr_stmt|;
comment|// underscores are delimiters, but not in email addresses (below)
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"word_having_underscore"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"word"
block|,
literal|"having"
block|,
literal|"underscore"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"word_with_underscore_and_stopwords"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"word"
block|,
literal|"underscore"
block|,
literal|"stopwords"
block|}
argument_list|)
expr_stmt|;
comment|// other delimiters: "-", "/", ","
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"some-dashed-phrase"
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
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"dogs,chase,cats"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"dogs"
block|,
literal|"chase"
block|,
literal|"cats"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ac/dc"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ac"
block|,
literal|"dc"
block|}
argument_list|)
expr_stmt|;
comment|// internal apostrophes: O'Reilly, you're, O'Reilly's
comment|// possessives are actually removed by StardardFilter, not the tokenizer
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"O'Reilly"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"o'reilly"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"you're"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"you're"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"she's"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"she"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Jim's"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"jim"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"don't"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"don't"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"O'Reilly's"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"o'reilly"
block|}
argument_list|)
expr_stmt|;
comment|// t and s had been stopwords in Lucene<= 2.0, which made it impossible
comment|// to correctly search for these terms:
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"s-class"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"s"
block|,
literal|"class"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"t-com"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"t"
block|,
literal|"com"
block|}
argument_list|)
expr_stmt|;
comment|// 'a' is still a stopword:
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"a-class"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"class"
block|}
argument_list|)
expr_stmt|;
comment|// company names
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"AT&T"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"at&t"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Excite@Home"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"excite@home"
block|}
argument_list|)
expr_stmt|;
comment|// domain names
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"www.nutch.org"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"www.nutch.org"
block|}
argument_list|)
expr_stmt|;
comment|// email addresses, possibly with underscores, periods, etc
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"test@example.com"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"test@example.com"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"first.lastname@example.com"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"first.lastname@example.com"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"first_lastname@example.com"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"first_lastname@example.com"
block|}
argument_list|)
expr_stmt|;
comment|// floating point, serial, model numbers, ip addresses, etc.
comment|// every other segment must have at least one digit
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"21.35"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"21.35"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"R2D2 C3PO"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"r2d2"
block|,
literal|"c3po"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"216.239.63.104"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"216.239.63.104"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"1-2-3"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"1-2-3"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"a1-b2-c3"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a1-b2-c3"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"a1-b-c3"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a1-b-c3"
block|}
argument_list|)
expr_stmt|;
comment|// numbers
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"David has 5000 bones"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"david"
block|,
literal|"has"
block|,
literal|"5000"
block|,
literal|"bones"
block|}
argument_list|)
expr_stmt|;
comment|// various
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"C embedded developers wanted"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"c"
block|,
literal|"embedded"
block|,
literal|"developers"
block|,
literal|"wanted"
block|}
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
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"foo      bar .  FOO<> BAR"
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
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"\"QUOTED\" word"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"quoted"
block|,
literal|"word"
block|}
argument_list|)
expr_stmt|;
comment|// acronyms have their dots stripped
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"U.S.A."
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"usa"
block|}
argument_list|)
expr_stmt|;
comment|// It would be nice to change the grammar in StandardTokenizer.jj to make "C#" and "C++" end up as tokens.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"C++"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"c"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"C#"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"c"
block|}
argument_list|)
expr_stmt|;
comment|// Korean words
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ìëíì¸ì íê¸ìëë¤"
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
block|}
end_class
end_unit
