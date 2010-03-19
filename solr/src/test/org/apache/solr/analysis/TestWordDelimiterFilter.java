begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|miscellaneous
operator|.
name|SingleTokenTokenStream
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
name|PositionIncrementAttribute
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
name|solr
operator|.
name|SolrTestCaseJ4
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
operator|.
name|BaseTokenTestCase
operator|.
name|*
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
name|HashSet
import|;
end_import
begin_comment
comment|/**  * New WordDelimiterFilter tests... most of the tests are in ConvertedLegacyTest  */
end_comment
begin_class
DECL|class|TestWordDelimiterFilter
specifier|public
class|class
name|TestWordDelimiterFilter
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
DECL|method|posTst
specifier|public
name|void
name|posTst
parameter_list|(
name|String
name|v1
parameter_list|,
name|String
name|v2
parameter_list|,
name|String
name|s1
parameter_list|,
name|String
name|s2
parameter_list|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"42"
argument_list|,
literal|"subword"
argument_list|,
name|v1
argument_list|,
literal|"subword"
argument_list|,
name|v2
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// there is a positionIncrementGap of 100 between field values, so
comment|// we test if that was maintained.
name|assertQ
argument_list|(
literal|"position increment lost"
argument_list|,
name|req
argument_list|(
literal|"+id:42 +subword:\""
operator|+
name|s1
operator|+
literal|' '
operator|+
name|s2
operator|+
literal|"\"~90"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"position increment lost"
argument_list|,
name|req
argument_list|(
literal|"+id:42 +subword:\""
operator|+
name|s1
operator|+
literal|' '
operator|+
name|s2
operator|+
literal|"\"~110"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRetainPositionIncrement
specifier|public
name|void
name|testRetainPositionIncrement
parameter_list|()
block|{
name|posTst
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|posTst
argument_list|(
literal|"-foo-"
argument_list|,
literal|"-bar-"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|posTst
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
literal|"-foo-"
argument_list|,
literal|"-bar-"
argument_list|)
expr_stmt|;
name|posTst
argument_list|(
literal|"123"
argument_list|,
literal|"456"
argument_list|,
literal|"123"
argument_list|,
literal|"456"
argument_list|)
expr_stmt|;
name|posTst
argument_list|(
literal|"/123/"
argument_list|,
literal|"/456/"
argument_list|,
literal|"123"
argument_list|,
literal|"456"
argument_list|)
expr_stmt|;
name|posTst
argument_list|(
literal|"/123/abc"
argument_list|,
literal|"qwe/456/"
argument_list|,
literal|"abc"
argument_list|,
literal|"qwe"
argument_list|)
expr_stmt|;
name|posTst
argument_list|(
literal|"zoo-foo"
argument_list|,
literal|"bar-baz"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|posTst
argument_list|(
literal|"zoo-foo-123"
argument_list|,
literal|"456-bar-baz"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoGenerationEdgeCase
specifier|public
name|void
name|testNoGenerationEdgeCase
parameter_list|()
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"222"
argument_list|,
literal|"numberpartfail"
argument_list|,
literal|"123.123.123.123"
argument_list|)
argument_list|)
expr_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIgnoreCaseChange
specifier|public
name|void
name|testIgnoreCaseChange
parameter_list|()
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"43"
argument_list|,
literal|"wdf_nocase"
argument_list|,
literal|"HellO WilliAM"
argument_list|,
literal|"subword"
argument_list|,
literal|"GoodBye JonEs"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"no case change"
argument_list|,
name|req
argument_list|(
literal|"wdf_nocase:(hell o am)"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"case change"
argument_list|,
name|req
argument_list|(
literal|"subword:(good jon)"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPreserveOrignalTrue
specifier|public
name|void
name|testPreserveOrignalTrue
parameter_list|()
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"144"
argument_list|,
literal|"wdf_preserve"
argument_list|,
literal|"404-123"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"preserving original word"
argument_list|,
name|req
argument_list|(
literal|"wdf_preserve:404"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"preserving original word"
argument_list|,
name|req
argument_list|(
literal|"wdf_preserve:123"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"preserving original word"
argument_list|,
name|req
argument_list|(
literal|"wdf_preserve:404-123*"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
block|}
comment|/***   public void testPerformance() throws IOException {     String s = "now is the time-for all good men to come to-the aid of their country.";     Token tok = new Token();     long start = System.currentTimeMillis();     int ret=0;     for (int i=0; i<1000000; i++) {       StringReader r = new StringReader(s);       TokenStream ts = new WhitespaceTokenizer(r);       ts = new WordDelimiterFilter(ts, 1,1,1,1,0);        while (ts.next(tok) != null) ret++;     }      System.out.println("ret="+ret+" time="+(System.currentTimeMillis()-start));   }   ***/
annotation|@
name|Test
DECL|method|testOffsets
specifier|public
name|void
name|testOffsets
parameter_list|()
throws|throws
name|IOException
block|{
comment|// test that subwords and catenated subwords have
comment|// the correct offsets.
name|WordDelimiterFilter
name|wdf
init|=
operator|new
name|WordDelimiterFilter
argument_list|(
operator|new
name|SingleTokenTokenStream
argument_list|(
operator|new
name|Token
argument_list|(
literal|"foo-bar"
argument_list|,
literal|5
argument_list|,
literal|12
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|wdf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"bar"
block|,
literal|"foobar"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|5
block|,
literal|9
block|,
literal|5
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|8
block|,
literal|12
block|,
literal|12
block|}
argument_list|)
expr_stmt|;
name|wdf
operator|=
operator|new
name|WordDelimiterFilter
argument_list|(
operator|new
name|SingleTokenTokenStream
argument_list|(
operator|new
name|Token
argument_list|(
literal|"foo-bar"
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|wdf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"bar"
block|,
literal|"foobar"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|5
block|,
literal|5
block|,
literal|5
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|6
block|,
literal|6
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOffsetChange
specifier|public
name|void
name|testOffsetChange
parameter_list|()
throws|throws
name|Exception
block|{
name|WordDelimiterFilter
name|wdf
init|=
operator|new
name|WordDelimiterFilter
argument_list|(
operator|new
name|SingleTokenTokenStream
argument_list|(
operator|new
name|Token
argument_list|(
literal|"Ã¼belkeit)"
argument_list|,
literal|7
argument_list|,
literal|16
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|wdf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ã¼belkeit"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|7
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|15
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOffsetChange2
specifier|public
name|void
name|testOffsetChange2
parameter_list|()
throws|throws
name|Exception
block|{
name|WordDelimiterFilter
name|wdf
init|=
operator|new
name|WordDelimiterFilter
argument_list|(
operator|new
name|SingleTokenTokenStream
argument_list|(
operator|new
name|Token
argument_list|(
literal|"(Ã¼belkeit"
argument_list|,
literal|7
argument_list|,
literal|17
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|wdf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ã¼belkeit"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|8
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|17
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOffsetChange3
specifier|public
name|void
name|testOffsetChange3
parameter_list|()
throws|throws
name|Exception
block|{
name|WordDelimiterFilter
name|wdf
init|=
operator|new
name|WordDelimiterFilter
argument_list|(
operator|new
name|SingleTokenTokenStream
argument_list|(
operator|new
name|Token
argument_list|(
literal|"(Ã¼belkeit"
argument_list|,
literal|7
argument_list|,
literal|16
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|wdf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ã¼belkeit"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|8
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|16
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOffsetChange4
specifier|public
name|void
name|testOffsetChange4
parameter_list|()
throws|throws
name|Exception
block|{
name|WordDelimiterFilter
name|wdf
init|=
operator|new
name|WordDelimiterFilter
argument_list|(
operator|new
name|SingleTokenTokenStream
argument_list|(
operator|new
name|Token
argument_list|(
literal|"(foo,bar)"
argument_list|,
literal|7
argument_list|,
literal|16
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|wdf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"bar"
block|,
literal|"foobar"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|8
block|,
literal|12
block|,
literal|8
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|11
block|,
literal|15
block|,
literal|15
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAlphaNumericWords
specifier|public
name|void
name|testAlphaNumericWords
parameter_list|()
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"68"
argument_list|,
literal|"numericsubword"
argument_list|,
literal|"Java/J2SE"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"j2se found"
argument_list|,
name|req
argument_list|(
literal|"numericsubword:(J2SE)"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"no j2 or se"
argument_list|,
name|req
argument_list|(
literal|"numericsubword:(J2 OR SE)"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testProtectedWords
specifier|public
name|void
name|testProtectedWords
parameter_list|()
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"70"
argument_list|,
literal|"protectedsubword"
argument_list|,
literal|"c# c++ .net Java/J2SE"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"java found"
argument_list|,
name|req
argument_list|(
literal|"protectedsubword:(java)"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|".net found"
argument_list|,
name|req
argument_list|(
literal|"protectedsubword:(.net)"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"c# found"
argument_list|,
name|req
argument_list|(
literal|"protectedsubword:(c#)"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"c++ found"
argument_list|,
name|req
argument_list|(
literal|"protectedsubword:(c++)"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"c found?"
argument_list|,
name|req
argument_list|(
literal|"protectedsubword:c"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"net found?"
argument_list|,
name|req
argument_list|(
literal|"protectedsubword:net"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
block|}
DECL|method|doSplit
specifier|public
name|void
name|doSplit
parameter_list|(
specifier|final
name|String
name|input
parameter_list|,
name|String
modifier|...
name|output
parameter_list|)
throws|throws
name|Exception
block|{
name|WordDelimiterFilter
name|wdf
init|=
operator|new
name|WordDelimiterFilter
argument_list|(
operator|new
name|KeywordTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|wdf
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSplits
specifier|public
name|void
name|testSplits
parameter_list|()
throws|throws
name|Exception
block|{
name|doSplit
argument_list|(
literal|"basic-split"
argument_list|,
literal|"basic"
argument_list|,
literal|"split"
argument_list|)
expr_stmt|;
name|doSplit
argument_list|(
literal|"camelCase"
argument_list|,
literal|"camel"
argument_list|,
literal|"Case"
argument_list|)
expr_stmt|;
comment|// non-space marking symbol shouldn't cause split
comment|// this is an example in Thai
name|doSplit
argument_list|(
literal|"\u0e1a\u0e49\u0e32\u0e19"
argument_list|,
literal|"\u0e1a\u0e49\u0e32\u0e19"
argument_list|)
expr_stmt|;
comment|// possessive followed by delimiter
name|doSplit
argument_list|(
literal|"test's'"
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
comment|// some russian upper and lowercase
name|doSplit
argument_list|(
literal|"Ð Ð¾Ð±ÐµÑÑ"
argument_list|,
literal|"Ð Ð¾Ð±ÐµÑÑ"
argument_list|)
expr_stmt|;
comment|// now cause a split (russian camelCase)
name|doSplit
argument_list|(
literal|"Ð Ð¾Ð±ÐÑÑ"
argument_list|,
literal|"Ð Ð¾Ð±"
argument_list|,
literal|"ÐÑÑ"
argument_list|)
expr_stmt|;
comment|// a composed titlecase character, don't split
name|doSplit
argument_list|(
literal|"aÇungla"
argument_list|,
literal|"aÇungla"
argument_list|)
expr_stmt|;
comment|// a modifier letter, don't split
name|doSplit
argument_list|(
literal|"Ø³ÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙØ§Ù"
argument_list|,
literal|"Ø³ÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙØ§Ù"
argument_list|)
expr_stmt|;
comment|// enclosing mark, don't split
name|doSplit
argument_list|(
literal|"Ûtest"
argument_list|,
literal|"Ûtest"
argument_list|)
expr_stmt|;
comment|// combining spacing mark (the virama), don't split
name|doSplit
argument_list|(
literal|"à¤¹à¤¿à¤¨à¥à¤¦à¥"
argument_list|,
literal|"à¤¹à¤¿à¤¨à¥à¤¦à¥"
argument_list|)
expr_stmt|;
comment|// don't split non-ascii digits
name|doSplit
argument_list|(
literal|"Ù¡Ù¢Ù£Ù¤"
argument_list|,
literal|"Ù¡Ù¢Ù£Ù¤"
argument_list|)
expr_stmt|;
comment|// don't split supplementaries into unpaired surrogates
name|doSplit
argument_list|(
literal|"ð ð "
argument_list|,
literal|"ð ð "
argument_list|)
expr_stmt|;
block|}
DECL|method|doSplitPossessive
specifier|public
name|void
name|doSplitPossessive
parameter_list|(
name|int
name|stemPossessive
parameter_list|,
specifier|final
name|String
name|input
parameter_list|,
specifier|final
name|String
modifier|...
name|output
parameter_list|)
throws|throws
name|Exception
block|{
name|WordDelimiterFilter
name|wdf
init|=
operator|new
name|WordDelimiterFilter
argument_list|(
operator|new
name|KeywordTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
name|stemPossessive
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|wdf
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
comment|/*    * Test option that allows disabling the special "'s" stemming, instead treating the single quote like other delimiters.     */
annotation|@
name|Test
DECL|method|testPossessives
specifier|public
name|void
name|testPossessives
parameter_list|()
throws|throws
name|Exception
block|{
name|doSplitPossessive
argument_list|(
literal|1
argument_list|,
literal|"ra's"
argument_list|,
literal|"ra"
argument_list|)
expr_stmt|;
name|doSplitPossessive
argument_list|(
literal|0
argument_list|,
literal|"ra's"
argument_list|,
literal|"ra"
argument_list|,
literal|"s"
argument_list|)
expr_stmt|;
block|}
comment|/*    * Set a large position increment gap of 10 if the token is "largegap" or "/"    */
DECL|class|LargePosIncTokenFilter
specifier|private
specifier|final
class|class
name|LargePosIncTokenFilter
extends|extends
name|TokenFilter
block|{
DECL|field|termAtt
specifier|private
name|TermAttribute
name|termAtt
decl_stmt|;
DECL|field|posIncAtt
specifier|private
name|PositionIncrementAttribute
name|posIncAtt
decl_stmt|;
DECL|method|LargePosIncTokenFilter
specifier|protected
name|LargePosIncTokenFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|termAtt
operator|=
operator|(
name|TermAttribute
operator|)
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|posIncAtt
operator|=
operator|(
name|PositionIncrementAttribute
operator|)
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
if|if
condition|(
name|termAtt
operator|.
name|term
argument_list|()
operator|.
name|equals
argument_list|(
literal|"largegap"
argument_list|)
operator|||
name|termAtt
operator|.
name|term
argument_list|()
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
condition|)
name|posIncAtt
operator|.
name|setPositionIncrement
argument_list|(
literal|10
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testPositionIncrements
specifier|public
name|void
name|testPositionIncrements
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CharArraySet
name|protWords
init|=
operator|new
name|CharArraySet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"NUTCH"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|/* analyzer that uses whitespace + wdf */
name|Analyzer
name|a
init|=
operator|new
name|Analyzer
argument_list|()
block|{
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|field
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|WordDelimiterFilter
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|reader
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|protWords
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/* in this case, works as expected. */
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"LUCENE / SOLR"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"LUCENE"
block|,
literal|"SOLR"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|9
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|13
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
comment|/* only in this case, posInc of 2 ?! */
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"LUCENE / solR"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"LUCENE"
block|,
literal|"sol"
block|,
literal|"R"
block|,
literal|"solR"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|9
block|,
literal|12
block|,
literal|9
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|12
block|,
literal|13
block|,
literal|13
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
literal|0
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"LUCENE / NUTCH SOLR"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"LUCENE"
block|,
literal|"NUTCH"
block|,
literal|"SOLR"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|9
block|,
literal|15
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|14
block|,
literal|19
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
block|}
argument_list|)
expr_stmt|;
comment|/* analyzer that will consume tokens with large position increments */
name|Analyzer
name|a2
init|=
operator|new
name|Analyzer
argument_list|()
block|{
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|field
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|WordDelimiterFilter
argument_list|(
operator|new
name|LargePosIncTokenFilter
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|reader
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|protWords
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/* increment of "largegap" is preserved */
name|assertAnalyzesTo
argument_list|(
name|a2
argument_list|,
literal|"LUCENE largegap SOLR"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"LUCENE"
block|,
literal|"largegap"
block|,
literal|"SOLR"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|7
block|,
literal|16
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|15
block|,
literal|20
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|10
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
comment|/* the "/" had a position increment of 10, where did it go?!?!! */
name|assertAnalyzesTo
argument_list|(
name|a2
argument_list|,
literal|"LUCENE / SOLR"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"LUCENE"
block|,
literal|"SOLR"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|9
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|13
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|11
block|}
argument_list|)
expr_stmt|;
comment|/* in this case, the increment of 10 from the "/" is carried over */
name|assertAnalyzesTo
argument_list|(
name|a2
argument_list|,
literal|"LUCENE / solR"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"LUCENE"
block|,
literal|"sol"
block|,
literal|"R"
block|,
literal|"solR"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|9
block|,
literal|12
block|,
literal|9
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|12
block|,
literal|13
block|,
literal|13
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|11
block|,
literal|1
block|,
literal|0
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a2
argument_list|,
literal|"LUCENE / NUTCH SOLR"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"LUCENE"
block|,
literal|"NUTCH"
block|,
literal|"SOLR"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|9
block|,
literal|15
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|14
block|,
literal|19
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|11
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
