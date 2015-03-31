begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.standard
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|standard
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|TextField
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
name|index
operator|.
name|DirectoryReader
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
name|index
operator|.
name|PostingsEnum
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
name|index
operator|.
name|IndexReader
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
name|index
operator|.
name|IndexWriter
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
name|index
operator|.
name|IndexWriterConfig
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
name|index
operator|.
name|MultiFields
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
name|index
operator|.
name|Term
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
name|search
operator|.
name|DocIdSetIterator
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
name|store
operator|.
name|RAMDirectory
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
name|BytesRef
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
name|util
operator|.
name|Arrays
import|;
end_import
begin_comment
comment|/** tests for classicanalyzer */
end_comment
begin_class
DECL|class|TestClassicAnalyzer
specifier|public
class|class
name|TestClassicAnalyzer
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|a
specifier|private
name|Analyzer
name|a
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
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
name|a
operator|=
operator|new
name|ClassicAnalyzer
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|a
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testMaxTermLength
specifier|public
name|void
name|testMaxTermLength
parameter_list|()
throws|throws
name|Exception
block|{
name|ClassicAnalyzer
name|sa
init|=
operator|new
name|ClassicAnalyzer
argument_list|()
decl_stmt|;
name|sa
operator|.
name|setMaxTokenLength
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|sa
argument_list|,
literal|"ab cd toolong xy z"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ab"
block|,
literal|"cd"
block|,
literal|"xy"
block|,
literal|"z"
block|}
argument_list|)
expr_stmt|;
name|sa
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testMaxTermLength2
specifier|public
name|void
name|testMaxTermLength2
parameter_list|()
throws|throws
name|Exception
block|{
name|ClassicAnalyzer
name|sa
init|=
operator|new
name|ClassicAnalyzer
argument_list|()
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|sa
argument_list|,
literal|"ab cd toolong xy z"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ab"
block|,
literal|"cd"
block|,
literal|"toolong"
block|,
literal|"xy"
block|,
literal|"z"
block|}
argument_list|)
expr_stmt|;
name|sa
operator|.
name|setMaxTokenLength
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|sa
argument_list|,
literal|"ab cd toolong xy z"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ab"
block|,
literal|"cd"
block|,
literal|"xy"
block|,
literal|"z"
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
literal|2
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
name|sa
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testMaxTermLength3
specifier|public
name|void
name|testMaxTermLength3
parameter_list|()
throws|throws
name|Exception
block|{
name|char
index|[]
name|chars
init|=
operator|new
name|char
index|[
literal|255
index|]
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
literal|255
condition|;
name|i
operator|++
control|)
name|chars
index|[
name|i
index|]
operator|=
literal|'a'
expr_stmt|;
name|String
name|longTerm
init|=
operator|new
name|String
argument_list|(
name|chars
argument_list|,
literal|0
argument_list|,
literal|255
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ab cd "
operator|+
name|longTerm
operator|+
literal|" xy z"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ab"
block|,
literal|"cd"
block|,
name|longTerm
block|,
literal|"xy"
block|,
literal|"z"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ab cd "
operator|+
name|longTerm
operator|+
literal|"a xy z"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ab"
block|,
literal|"cd"
block|,
literal|"xy"
block|,
literal|"z"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testAlphanumeric
specifier|public
name|void
name|testAlphanumeric
parameter_list|()
throws|throws
name|Exception
block|{
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
block|}
DECL|method|testUnderscores
specifier|public
name|void
name|testUnderscores
parameter_list|()
throws|throws
name|Exception
block|{
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
block|}
DECL|method|testDelimiters
specifier|public
name|void
name|testDelimiters
parameter_list|()
throws|throws
name|Exception
block|{
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
block|}
DECL|method|testApostrophes
specifier|public
name|void
name|testApostrophes
parameter_list|()
throws|throws
name|Exception
block|{
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
block|}
DECL|method|testTSADash
specifier|public
name|void
name|testTSADash
parameter_list|()
throws|throws
name|Exception
block|{
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
block|}
DECL|method|testCompanyNames
specifier|public
name|void
name|testCompanyNames
parameter_list|()
throws|throws
name|Exception
block|{
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
block|}
DECL|method|testLucene1140
specifier|public
name|void
name|testLucene1140
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|ClassicAnalyzer
name|analyzer
init|=
operator|new
name|ClassicAnalyzer
argument_list|()
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"www.nutch.org."
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"www.nutch.org"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<HOST>"
block|}
argument_list|)
expr_stmt|;
name|analyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Should not throw an NPE and it did"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testDomainNames
specifier|public
name|void
name|testDomainNames
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Current lucene should not show the bug
name|ClassicAnalyzer
name|a2
init|=
operator|new
name|ClassicAnalyzer
argument_list|()
decl_stmt|;
comment|// domain names
name|assertAnalyzesTo
argument_list|(
name|a2
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
comment|//Notice the trailing .  See https://issues.apache.org/jira/browse/LUCENE-1068.
comment|// the following should be recognized as HOST:
name|assertAnalyzesTo
argument_list|(
name|a2
argument_list|,
literal|"www.nutch.org."
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"www.nutch.org"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<HOST>"
block|}
argument_list|)
expr_stmt|;
comment|// 2.3 should show the bug. But, alas, it's obsolete, we don't support it.
comment|// a2 = new ClassicAnalyzer(org.apache.lucene.util.Version.LUCENE_23);
comment|// assertAnalyzesTo(a2, "www.nutch.org.", new String[]{ "wwwnutchorg" }, new String[] { "<ACRONYM>" });
comment|// 2.4 should not show the bug. But, alas, it's also obsolete,
comment|// so we check latest released (Robert's gonna break this on 4.0 soon :) )
name|a2
operator|.
name|close
argument_list|()
expr_stmt|;
name|a2
operator|=
operator|new
name|ClassicAnalyzer
argument_list|()
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a2
argument_list|,
literal|"www.nutch.org."
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"www.nutch.org"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<HOST>"
block|}
argument_list|)
expr_stmt|;
name|a2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testEMailAddresses
specifier|public
name|void
name|testEMailAddresses
parameter_list|()
throws|throws
name|Exception
block|{
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
block|}
DECL|method|testNumeric
specifier|public
name|void
name|testNumeric
parameter_list|()
throws|throws
name|Exception
block|{
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
block|}
DECL|method|testTextWithNumbers
specifier|public
name|void
name|testTextWithNumbers
parameter_list|()
throws|throws
name|Exception
block|{
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
block|}
DECL|method|testVariousText
specifier|public
name|void
name|testVariousText
parameter_list|()
throws|throws
name|Exception
block|{
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
block|}
DECL|method|testAcronyms
specifier|public
name|void
name|testAcronyms
parameter_list|()
throws|throws
name|Exception
block|{
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
block|}
DECL|method|testCPlusPlusHash
specifier|public
name|void
name|testCPlusPlusHash
parameter_list|()
throws|throws
name|Exception
block|{
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
block|}
DECL|method|testKorean
specifier|public
name|void
name|testKorean
parameter_list|()
throws|throws
name|Exception
block|{
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
comment|// Compliance with the "old" JavaCC-based analyzer, see:
comment|// https://issues.apache.org/jira/browse/LUCENE-966#action_12516752
DECL|method|testComplianceFileName
specifier|public
name|void
name|testComplianceFileName
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"2004.jpg"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"2004.jpg"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<HOST>"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testComplianceNumericIncorrect
specifier|public
name|void
name|testComplianceNumericIncorrect
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"62.46"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"62.46"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<HOST>"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testComplianceNumericLong
specifier|public
name|void
name|testComplianceNumericLong
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"978-0-94045043-1"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"978-0-94045043-1"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<NUM>"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testComplianceNumericFile
specifier|public
name|void
name|testComplianceNumericFile
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"78academyawards/rules/rule02.html"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"78academyawards/rules/rule02.html"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<NUM>"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testComplianceNumericWithUnderscores
specifier|public
name|void
name|testComplianceNumericWithUnderscores
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"2006-03-11t082958z_01_ban130523_rtridst_0_ozabs"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"2006-03-11t082958z_01_ban130523_rtridst_0_ozabs"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<NUM>"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testComplianceNumericWithDash
specifier|public
name|void
name|testComplianceNumericWithDash
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"mid-20th"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mid-20th"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<NUM>"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testComplianceManyTokens
specifier|public
name|void
name|testComplianceManyTokens
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"/money.cnn.com/magazines/fortune/fortune_archive/2007/03/19/8402357/index.htm "
operator|+
literal|"safari-0-sheikh-zayed-grand-mosque.jpg"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"money.cnn.com"
block|,
literal|"magazines"
block|,
literal|"fortune"
block|,
literal|"fortune"
block|,
literal|"archive/2007/03/19/8402357"
block|,
literal|"index.htm"
block|,
literal|"safari-0-sheikh"
block|,
literal|"zayed"
block|,
literal|"grand"
block|,
literal|"mosque.jpg"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<HOST>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<NUM>"
block|,
literal|"<HOST>"
block|,
literal|"<NUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<HOST>"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testJava14BWCompatibility
specifier|public
name|void
name|testJava14BWCompatibility
parameter_list|()
throws|throws
name|Exception
block|{
name|ClassicAnalyzer
name|sa
init|=
operator|new
name|ClassicAnalyzer
argument_list|()
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|sa
argument_list|,
literal|"test\u02C6test"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"test"
block|,
literal|"test"
block|}
argument_list|)
expr_stmt|;
name|sa
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Make sure we skip wicked long terms.   */
DECL|method|testWickedLongTerm
specifier|public
name|void
name|testWickedLongTerm
parameter_list|()
throws|throws
name|IOException
block|{
name|RAMDirectory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|ClassicAnalyzer
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|analyzer
argument_list|)
argument_list|)
decl_stmt|;
name|char
index|[]
name|chars
init|=
operator|new
name|char
index|[
name|IndexWriter
operator|.
name|MAX_TERM_LENGTH
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|chars
argument_list|,
literal|'x'
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
specifier|final
name|String
name|bigTerm
init|=
operator|new
name|String
argument_list|(
name|chars
argument_list|)
decl_stmt|;
comment|// This produces a too-long term:
name|String
name|contents
init|=
literal|"abc xyz x"
operator|+
name|bigTerm
operator|+
literal|" another term"
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"content"
argument_list|,
name|contents
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// Make sure we can add another normal document
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"content"
argument_list|,
literal|"abc bbb ccc"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
comment|// Make sure all terms< max size were indexed
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"abc"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"bbb"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"term"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"another"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Make sure position is still incremented when
comment|// massive term is skipped:
name|PostingsEnum
name|tps
init|=
name|MultiFields
operator|.
name|getTermPositionsEnum
argument_list|(
name|reader
argument_list|,
name|MultiFields
operator|.
name|getLiveDocs
argument_list|(
name|reader
argument_list|)
argument_list|,
literal|"content"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"another"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tps
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tps
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|tps
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
comment|// Make sure the doc that has the massive term is in
comment|// the index:
name|assertEquals
argument_list|(
literal|"document with wicked long term should is not in the index!"
argument_list|,
literal|2
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Make sure we can add a document with exactly the
comment|// maximum length term, and search on that term:
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"content"
argument_list|,
name|bigTerm
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|ClassicAnalyzer
name|sa
init|=
operator|new
name|ClassicAnalyzer
argument_list|()
decl_stmt|;
name|sa
operator|.
name|setMaxTokenLength
argument_list|(
literal|100000
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|sa
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
name|bigTerm
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|analyzer
operator|.
name|close
argument_list|()
expr_stmt|;
name|sa
operator|.
name|close
argument_list|()
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
name|Analyzer
name|analyzer
init|=
operator|new
name|ClassicAnalyzer
argument_list|()
decl_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|analyzer
argument_list|,
literal|1000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
name|analyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** blast some random large strings through the analyzer */
DECL|method|testRandomHugeStrings
specifier|public
name|void
name|testRandomHugeStrings
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|ClassicAnalyzer
argument_list|()
decl_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|analyzer
argument_list|,
literal|100
operator|*
name|RANDOM_MULTIPLIER
argument_list|,
literal|8192
argument_list|)
expr_stmt|;
name|analyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
