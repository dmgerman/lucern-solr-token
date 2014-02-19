begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.charfilter
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|charfilter
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
name|ArrayList
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
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|CharFilter
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
name|util
operator|.
name|TestUtil
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
name|UnicodeUtil
import|;
end_import
begin_class
DECL|class|TestMappingCharFilter
specifier|public
class|class
name|TestMappingCharFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|normMap
name|NormalizeCharMap
name|normMap
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
name|NormalizeCharMap
operator|.
name|Builder
name|builder
init|=
operator|new
name|NormalizeCharMap
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
literal|"aa"
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
literal|"bbb"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
literal|"cccc"
argument_list|,
literal|"cc"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
literal|"h"
argument_list|,
literal|"i"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
literal|"j"
argument_list|,
literal|"jj"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
literal|"k"
argument_list|,
literal|"kkk"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
literal|"ll"
argument_list|,
literal|"llll"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
literal|"empty"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// BMP (surrogate pair):
name|builder
operator|.
name|add
argument_list|(
name|UnicodeUtil
operator|.
name|newString
argument_list|(
operator|new
name|int
index|[]
block|{
literal|0x1D122
block|}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|"fclef"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
literal|"\uff01"
argument_list|,
literal|"full-width-exclamation"
argument_list|)
expr_stmt|;
name|normMap
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
DECL|method|testReaderReset
specifier|public
name|void
name|testReaderReset
parameter_list|()
throws|throws
name|Exception
block|{
name|CharFilter
name|cs
init|=
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"x"
argument_list|)
argument_list|)
decl_stmt|;
name|char
index|[]
name|buf
init|=
operator|new
name|char
index|[
literal|10
index|]
decl_stmt|;
name|int
name|len
init|=
name|cs
operator|.
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'x'
argument_list|,
name|buf
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|len
operator|=
name|cs
operator|.
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|len
argument_list|)
expr_stmt|;
comment|// rewind
name|cs
operator|.
name|reset
argument_list|()
expr_stmt|;
name|len
operator|=
name|cs
operator|.
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'x'
argument_list|,
name|buf
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|testNothingChange
specifier|public
name|void
name|testNothingChange
parameter_list|()
throws|throws
name|Exception
block|{
name|CharFilter
name|cs
init|=
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"x"
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|whitespaceMockTokenizer
argument_list|(
name|cs
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
literal|"x"
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
literal|1
block|}
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|test1to1
specifier|public
name|void
name|test1to1
parameter_list|()
throws|throws
name|Exception
block|{
name|CharFilter
name|cs
init|=
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"h"
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|whitespaceMockTokenizer
argument_list|(
name|cs
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
literal|"i"
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
literal|1
block|}
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|test1to2
specifier|public
name|void
name|test1to2
parameter_list|()
throws|throws
name|Exception
block|{
name|CharFilter
name|cs
init|=
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"j"
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|whitespaceMockTokenizer
argument_list|(
name|cs
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
literal|"jj"
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
literal|1
block|}
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|test1to3
specifier|public
name|void
name|test1to3
parameter_list|()
throws|throws
name|Exception
block|{
name|CharFilter
name|cs
init|=
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"k"
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|whitespaceMockTokenizer
argument_list|(
name|cs
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
literal|"kkk"
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
literal|1
block|}
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|test2to4
specifier|public
name|void
name|test2to4
parameter_list|()
throws|throws
name|Exception
block|{
name|CharFilter
name|cs
init|=
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"ll"
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|whitespaceMockTokenizer
argument_list|(
name|cs
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
literal|"llll"
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
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
DECL|method|test2to1
specifier|public
name|void
name|test2to1
parameter_list|()
throws|throws
name|Exception
block|{
name|CharFilter
name|cs
init|=
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"aa"
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|whitespaceMockTokenizer
argument_list|(
name|cs
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
literal|"a"
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
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
DECL|method|test3to1
specifier|public
name|void
name|test3to1
parameter_list|()
throws|throws
name|Exception
block|{
name|CharFilter
name|cs
init|=
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"bbb"
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|whitespaceMockTokenizer
argument_list|(
name|cs
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
literal|"b"
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
literal|3
block|}
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
DECL|method|test4to2
specifier|public
name|void
name|test4to2
parameter_list|()
throws|throws
name|Exception
block|{
name|CharFilter
name|cs
init|=
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"cccc"
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|whitespaceMockTokenizer
argument_list|(
name|cs
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
literal|"cc"
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
literal|4
block|}
argument_list|,
literal|4
argument_list|)
expr_stmt|;
block|}
DECL|method|test5to0
specifier|public
name|void
name|test5to0
parameter_list|()
throws|throws
name|Exception
block|{
name|CharFilter
name|cs
init|=
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"empty"
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|whitespaceMockTokenizer
argument_list|(
name|cs
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|,
operator|new
name|int
index|[]
block|{}
argument_list|,
operator|new
name|int
index|[]
block|{}
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testNonBMPChar
specifier|public
name|void
name|testNonBMPChar
parameter_list|()
throws|throws
name|Exception
block|{
name|CharFilter
name|cs
init|=
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
operator|new
name|StringReader
argument_list|(
name|UnicodeUtil
operator|.
name|newString
argument_list|(
operator|new
name|int
index|[]
block|{
literal|0x1D122
block|}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|whitespaceMockTokenizer
argument_list|(
name|cs
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
literal|"fclef"
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
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
DECL|method|testFullWidthChar
specifier|public
name|void
name|testFullWidthChar
parameter_list|()
throws|throws
name|Exception
block|{
name|CharFilter
name|cs
init|=
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"\uff01"
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|whitespaceMockTokenizer
argument_list|(
name|cs
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
literal|"full-width-exclamation"
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
literal|1
block|}
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|//
comment|//                1111111111222
comment|//      01234567890123456789012
comment|//(in)  h i j k ll cccc bbb aa
comment|//
comment|//                1111111111222
comment|//      01234567890123456789012
comment|//(out) i i jj kkk llll cc b a
comment|//
comment|//    h, 0, 1 =>    i, 0, 1
comment|//    i, 2, 3 =>    i, 2, 3
comment|//    j, 4, 5 =>   jj, 4, 5
comment|//    k, 6, 7 =>  kkk, 6, 7
comment|//   ll, 8,10 => llll, 8,10
comment|// cccc,11,15 =>   cc,11,15
comment|//  bbb,16,19 =>    b,16,19
comment|//   aa,20,22 =>    a,20,22
comment|//
DECL|method|testTokenStream
specifier|public
name|void
name|testTokenStream
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|testString
init|=
literal|"h i j k ll cccc bbb aa"
decl_stmt|;
name|CharFilter
name|cs
init|=
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
operator|new
name|StringReader
argument_list|(
name|testString
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|whitespaceMockTokenizer
argument_list|(
name|cs
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
literal|"i"
block|,
literal|"i"
block|,
literal|"jj"
block|,
literal|"kkk"
block|,
literal|"llll"
block|,
literal|"cc"
block|,
literal|"b"
block|,
literal|"a"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|,
literal|4
block|,
literal|6
block|,
literal|8
block|,
literal|11
block|,
literal|16
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
literal|3
block|,
literal|5
block|,
literal|7
block|,
literal|10
block|,
literal|15
block|,
literal|19
block|,
literal|22
block|}
argument_list|,
name|testString
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//
comment|//
comment|//        0123456789
comment|//(in)    aaaa ll h
comment|//(out-1) aa llll i
comment|//(out-2) a llllllll i
comment|//
comment|// aaaa,0,4 => a,0,4
comment|//   ll,5,7 => llllllll,5,7
comment|//    h,8,9 => i,8,9
DECL|method|testChained
specifier|public
name|void
name|testChained
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|testString
init|=
literal|"aaaa ll h"
decl_stmt|;
name|CharFilter
name|cs
init|=
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
operator|new
name|StringReader
argument_list|(
name|testString
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|whitespaceMockTokenizer
argument_list|(
name|cs
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
literal|"a"
block|,
literal|"llllllll"
block|,
literal|"i"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|5
block|,
literal|8
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|4
block|,
literal|7
block|,
literal|9
block|}
argument_list|,
name|testString
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|analyzer
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
name|Tokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|tokenizer
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
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
name|reader
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|int
name|numRounds
init|=
name|RANDOM_MULTIPLIER
operator|*
literal|10000
decl_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|analyzer
argument_list|,
name|numRounds
argument_list|)
expr_stmt|;
block|}
comment|//@Ignore("wrong finalOffset: https://issues.apache.org/jira/browse/LUCENE-3971")
DECL|method|testFinalOffsetSpecialCase
specifier|public
name|void
name|testFinalOffsetSpecialCase
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|NormalizeCharMap
operator|.
name|Builder
name|builder
init|=
operator|new
name|NormalizeCharMap
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
literal|"t"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// even though this below rule has no effect, the test passes if you remove it!!
name|builder
operator|.
name|add
argument_list|(
literal|"tmakdbl"
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
specifier|final
name|NormalizeCharMap
name|map
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|Analyzer
name|analyzer
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
name|Tokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|tokenizer
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
name|MappingCharFilter
argument_list|(
name|map
argument_list|,
name|reader
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|String
name|text
init|=
literal|"gzw f quaxot"
decl_stmt|;
name|checkAnalysisConsistency
argument_list|(
name|random
argument_list|()
argument_list|,
name|analyzer
argument_list|,
literal|false
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
comment|//@Ignore("wrong finalOffset: https://issues.apache.org/jira/browse/LUCENE-3971")
DECL|method|testRandomMaps
specifier|public
name|void
name|testRandomMaps
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numIterations
init|=
name|atLeast
argument_list|(
literal|3
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
name|numIterations
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|NormalizeCharMap
name|map
init|=
name|randomMap
argument_list|()
decl_stmt|;
name|Analyzer
name|analyzer
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
name|Tokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|tokenizer
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
name|MappingCharFilter
argument_list|(
name|map
argument_list|,
name|reader
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|int
name|numRounds
init|=
literal|100
decl_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|analyzer
argument_list|,
name|numRounds
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|randomMap
specifier|private
name|NormalizeCharMap
name|randomMap
parameter_list|()
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|NormalizeCharMap
operator|.
name|Builder
name|builder
init|=
operator|new
name|NormalizeCharMap
operator|.
name|Builder
argument_list|()
decl_stmt|;
comment|// we can't add duplicate keys, or NormalizeCharMap gets angry
name|Set
argument_list|<
name|String
argument_list|>
name|keys
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|num
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
decl_stmt|;
comment|//System.out.println("NormalizeCharMap=");
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
name|key
init|=
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|keys
operator|.
name|contains
argument_list|(
name|key
argument_list|)
operator|&&
name|key
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|String
name|value
init|=
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|keys
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
comment|//System.out.println("mapping: '" + key + "' => '" + value + "'");
block|}
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|testRandomMaps2
specifier|public
name|void
name|testRandomMaps2
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numIterations
init|=
name|atLeast
argument_list|(
literal|3
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|numIterations
condition|;
name|iter
operator|++
control|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST iter="
operator|+
name|iter
argument_list|)
expr_stmt|;
block|}
specifier|final
name|char
name|endLetter
init|=
operator|(
name|char
operator|)
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|'b'
argument_list|,
literal|'z'
argument_list|)
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
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
specifier|final
name|NormalizeCharMap
operator|.
name|Builder
name|builder
init|=
operator|new
name|NormalizeCharMap
operator|.
name|Builder
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numMappings
init|=
name|atLeast
argument_list|(
literal|5
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  mappings:"
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|map
operator|.
name|size
argument_list|()
operator|<
name|numMappings
condition|)
block|{
specifier|final
name|String
name|key
init|=
name|TestUtil
operator|.
name|randomSimpleStringRange
argument_list|(
name|random
argument_list|,
literal|'a'
argument_list|,
name|endLetter
argument_list|,
literal|7
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|length
argument_list|()
operator|!=
literal|0
operator|&&
operator|!
name|map
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
specifier|final
name|String
name|value
init|=
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    "
operator|+
name|key
operator|+
literal|" -> "
operator|+
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|final
name|NormalizeCharMap
name|charMap
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  test random documents..."
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|iter2
init|=
literal|0
init|;
name|iter2
operator|<
literal|100
condition|;
name|iter2
operator|++
control|)
block|{
specifier|final
name|String
name|content
init|=
name|TestUtil
operator|.
name|randomSimpleStringRange
argument_list|(
name|random
argument_list|,
literal|'a'
argument_list|,
name|endLetter
argument_list|,
name|atLeast
argument_list|(
literal|1000
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  content="
operator|+
name|content
argument_list|)
expr_stmt|;
block|}
comment|// Do stupid dog-slow mapping:
comment|// Output string:
specifier|final
name|StringBuilder
name|output
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|// Maps output offset to input offset:
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|inputOffsets
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|cumDiff
init|=
literal|0
decl_stmt|;
name|int
name|charIdx
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|charIdx
operator|<
name|content
operator|.
name|length
argument_list|()
condition|)
block|{
name|int
name|matchLen
init|=
operator|-
literal|1
decl_stmt|;
name|String
name|matchRepl
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|ent
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|String
name|match
init|=
name|ent
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|charIdx
operator|+
name|match
operator|.
name|length
argument_list|()
operator|<=
name|content
operator|.
name|length
argument_list|()
condition|)
block|{
specifier|final
name|int
name|limit
init|=
name|charIdx
operator|+
name|match
operator|.
name|length
argument_list|()
decl_stmt|;
name|boolean
name|matches
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|charIdx2
init|=
name|charIdx
init|;
name|charIdx2
operator|<
name|limit
condition|;
name|charIdx2
operator|++
control|)
block|{
if|if
condition|(
name|match
operator|.
name|charAt
argument_list|(
name|charIdx2
operator|-
name|charIdx
argument_list|)
operator|!=
name|content
operator|.
name|charAt
argument_list|(
name|charIdx2
argument_list|)
condition|)
block|{
name|matches
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|matches
condition|)
block|{
specifier|final
name|String
name|repl
init|=
name|ent
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|match
operator|.
name|length
argument_list|()
operator|>
name|matchLen
condition|)
block|{
comment|// Greedy: longer match wins
name|matchLen
operator|=
name|match
operator|.
name|length
argument_list|()
expr_stmt|;
name|matchRepl
operator|=
name|repl
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|matchLen
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// We found a match here!
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    match="
operator|+
name|content
operator|.
name|substring
argument_list|(
name|charIdx
argument_list|,
name|charIdx
operator|+
name|matchLen
argument_list|)
operator|+
literal|" @ off="
operator|+
name|charIdx
operator|+
literal|" repl="
operator|+
name|matchRepl
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|append
argument_list|(
name|matchRepl
argument_list|)
expr_stmt|;
specifier|final
name|int
name|minLen
init|=
name|Math
operator|.
name|min
argument_list|(
name|matchLen
argument_list|,
name|matchRepl
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
comment|// Common part, directly maps back to input
comment|// offset:
for|for
control|(
name|int
name|outIdx
init|=
literal|0
init|;
name|outIdx
operator|<
name|minLen
condition|;
name|outIdx
operator|++
control|)
block|{
name|inputOffsets
operator|.
name|add
argument_list|(
name|output
operator|.
name|length
argument_list|()
operator|-
name|matchRepl
operator|.
name|length
argument_list|()
operator|+
name|outIdx
operator|+
name|cumDiff
argument_list|)
expr_stmt|;
block|}
name|cumDiff
operator|+=
name|matchLen
operator|-
name|matchRepl
operator|.
name|length
argument_list|()
expr_stmt|;
name|charIdx
operator|+=
name|matchLen
expr_stmt|;
if|if
condition|(
name|matchRepl
operator|.
name|length
argument_list|()
operator|<
name|matchLen
condition|)
block|{
comment|// Replacement string is shorter than matched
comment|// input: nothing to do
block|}
elseif|else
if|if
condition|(
name|matchRepl
operator|.
name|length
argument_list|()
operator|>
name|matchLen
condition|)
block|{
comment|// Replacement string is longer than matched
comment|// input: for all the "extra" chars we map
comment|// back to a single input offset:
for|for
control|(
name|int
name|outIdx
init|=
name|matchLen
init|;
name|outIdx
operator|<
name|matchRepl
operator|.
name|length
argument_list|()
condition|;
name|outIdx
operator|++
control|)
block|{
name|inputOffsets
operator|.
name|add
argument_list|(
name|output
operator|.
name|length
argument_list|()
operator|+
name|cumDiff
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Same length: no change to offset
block|}
assert|assert
name|inputOffsets
operator|.
name|size
argument_list|()
operator|==
name|output
operator|.
name|length
argument_list|()
operator|:
literal|"inputOffsets.size()="
operator|+
name|inputOffsets
operator|.
name|size
argument_list|()
operator|+
literal|" vs output.length()="
operator|+
name|output
operator|.
name|length
argument_list|()
assert|;
block|}
else|else
block|{
name|inputOffsets
operator|.
name|add
argument_list|(
name|output
operator|.
name|length
argument_list|()
operator|+
name|cumDiff
argument_list|)
expr_stmt|;
name|output
operator|.
name|append
argument_list|(
name|content
operator|.
name|charAt
argument_list|(
name|charIdx
argument_list|)
argument_list|)
expr_stmt|;
name|charIdx
operator|++
expr_stmt|;
block|}
block|}
specifier|final
name|String
name|expected
init|=
name|output
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"    expected:"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|charIdx2
init|=
literal|0
init|;
name|charIdx2
operator|<
name|expected
operator|.
name|length
argument_list|()
condition|;
name|charIdx2
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|" "
operator|+
name|expected
operator|.
name|charAt
argument_list|(
name|charIdx2
argument_list|)
operator|+
literal|"/"
operator|+
name|inputOffsets
operator|.
name|get
argument_list|(
name|charIdx2
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
specifier|final
name|MappingCharFilter
name|mapFilter
init|=
operator|new
name|MappingCharFilter
argument_list|(
name|charMap
argument_list|,
operator|new
name|StringReader
argument_list|(
name|content
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|StringBuilder
name|actualBuilder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|actualInputOffsets
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
comment|// Now consume the actual mapFilter, somewhat randomly:
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
specifier|final
name|int
name|ch
init|=
name|mapFilter
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|ch
operator|==
operator|-
literal|1
condition|)
block|{
break|break;
block|}
name|actualBuilder
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|ch
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|)
index|]
decl_stmt|;
specifier|final
name|int
name|off
init|=
name|buffer
operator|.
name|length
operator|==
literal|1
condition|?
literal|0
else|:
name|random
operator|.
name|nextInt
argument_list|(
name|buffer
operator|.
name|length
operator|-
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|int
name|count
init|=
name|mapFilter
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
name|off
argument_list|,
name|buffer
operator|.
name|length
operator|-
name|off
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|==
operator|-
literal|1
condition|)
block|{
break|break;
block|}
else|else
block|{
name|actualBuilder
operator|.
name|append
argument_list|(
name|buffer
argument_list|,
name|off
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|7
condition|)
block|{
comment|// Map offsets
while|while
condition|(
name|actualInputOffsets
operator|.
name|size
argument_list|()
operator|<
name|actualBuilder
operator|.
name|length
argument_list|()
condition|)
block|{
name|actualInputOffsets
operator|.
name|add
argument_list|(
name|mapFilter
operator|.
name|correctOffset
argument_list|(
name|actualInputOffsets
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Finish mappping offsets
while|while
condition|(
name|actualInputOffsets
operator|.
name|size
argument_list|()
operator|<
name|actualBuilder
operator|.
name|length
argument_list|()
condition|)
block|{
name|actualInputOffsets
operator|.
name|add
argument_list|(
name|mapFilter
operator|.
name|correctOffset
argument_list|(
name|actualInputOffsets
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|actual
init|=
name|actualBuilder
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// Verify:
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|inputOffsets
argument_list|,
name|actualInputOffsets
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
