begin_unit
begin_package
DECL|package|org.apache.lucene.search.vectorhighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|vectorhighlight
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
name|BooleanQuery
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
name|BooleanClause
operator|.
name|Occur
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
name|WildcardQuery
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
name|vectorhighlight
operator|.
name|FieldTermStack
operator|.
name|TermInfo
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
begin_class
DECL|class|FieldTermStackTest
specifier|public
class|class
name|FieldTermStackTest
extends|extends
name|AbstractTestCase
block|{
DECL|method|test1Term
specifier|public
name|void
name|test1Term
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndex
argument_list|()
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|tq
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|stack
operator|.
name|termList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a(0,1,0)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a(2,3,1)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a(4,5,2)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a(12,13,6)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a(28,29,14)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a(32,33,16)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|test2Terms
specifier|public
name|void
name|test2Terms
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndex
argument_list|()
expr_stmt|;
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"b"
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"c"
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|query
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|stack
operator|.
name|termList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b(6,7,3)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b(8,9,4)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"c(10,11,5)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b(14,15,7)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b(16,17,8)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"c(18,19,9)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b(26,27,13)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b(30,31,15)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|test1Phrase
specifier|public
name|void
name|test1Phrase
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndex
argument_list|()
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|pqF
argument_list|(
literal|"c"
argument_list|,
literal|"d"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|stack
operator|.
name|termList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"c(10,11,5)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"c(18,19,9)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"d(20,21,10)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|makeIndex
specifier|private
name|void
name|makeIndex
parameter_list|()
throws|throws
name|Exception
block|{
comment|//           111111111122222
comment|// 0123456789012345678901234 (offsets)
comment|// a a a b b c a b b c d e f
comment|// 0 1 2 3 4 5 6 7 8 9101112 (position)
name|String
name|value1
init|=
literal|"a a a b b c a b b c d e f"
decl_stmt|;
comment|// 222233333
comment|// 678901234 (offsets)
comment|// b a b a f
comment|//1314151617 (position)
name|String
name|value2
init|=
literal|"b a b a f"
decl_stmt|;
name|make1dmfIndex
argument_list|(
name|value1
argument_list|,
name|value2
argument_list|)
expr_stmt|;
block|}
DECL|method|test1TermB
specifier|public
name|void
name|test1TermB
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndexB
argument_list|()
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|tq
argument_list|(
literal|"ab"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|stack
operator|.
name|termList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ab(2,4,2)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ab(6,8,6)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|test2TermsB
specifier|public
name|void
name|test2TermsB
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndexB
argument_list|()
expr_stmt|;
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"bc"
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
name|tq
argument_list|(
literal|"ef"
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|query
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|stack
operator|.
name|termList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bc(4,6,4)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bc(8,10,8)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ef(11,13,11)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|test1PhraseB
specifier|public
name|void
name|test1PhraseB
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndexB
argument_list|()
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|pqF
argument_list|(
literal|"ab"
argument_list|,
literal|"bb"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|stack
operator|.
name|termList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ab(2,4,2)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bb(3,5,3)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ab(6,8,6)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bb(7,9,7)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|makeIndexB
specifier|private
name|void
name|makeIndexB
parameter_list|()
throws|throws
name|Exception
block|{
comment|//                             1 11 11
comment|// 01 12 23 34 45 56 67 78 89 90 01 12 (offsets)
comment|// aa|aa|ab|bb|bc|ca|ab|bb|bc|cd|de|ef
comment|//  0  1  2  3  4  5  6  7  8  9 10 11 (position)
name|String
name|value
init|=
literal|"aaabbcabbcdef"
decl_stmt|;
name|make1dmfIndexB
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|test1PhraseShortMV
specifier|public
name|void
name|test1PhraseShortMV
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndexShortMV
argument_list|()
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|tq
argument_list|(
literal|"d"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stack
operator|.
name|termList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"d(9,10,3)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|test1PhraseLongMV
specifier|public
name|void
name|test1PhraseLongMV
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndexLongMV
argument_list|()
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|pqF
argument_list|(
literal|"search"
argument_list|,
literal|"engines"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|stack
operator|.
name|termList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"search(102,108,14)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"engines(109,116,15)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"search(157,163,24)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"engines(164,171,25)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|test1PhraseMVB
specifier|public
name|void
name|test1PhraseMVB
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndexLongMVB
argument_list|()
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|pqF
argument_list|(
literal|"sp"
argument_list|,
literal|"pe"
argument_list|,
literal|"ee"
argument_list|,
literal|"ed"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// "speed" -(2gram)-> "sp","pe","ee","ed"
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|stack
operator|.
name|termList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"sp(88,90,61)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"pe(89,91,62)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ee(90,92,63)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ed(91,93,64)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testWildcard
specifier|public
name|void
name|testWildcard
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndexLongMV
argument_list|()
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|F
argument_list|,
literal|"th*e"
argument_list|)
argument_list|)
argument_list|,
name|reader
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|stack
operator|.
name|termList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"the(15,18,2)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"these(133,138,20)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"the(153,156,23)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"the(195,198,31)"
argument_list|,
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testTermInfoComparisonConsistency
specifier|public
name|void
name|testTermInfoComparisonConsistency
parameter_list|()
block|{
name|TermInfo
name|a
init|=
operator|new
name|TermInfo
argument_list|(
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|TermInfo
name|b
init|=
operator|new
name|TermInfo
argument_list|(
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|TermInfo
name|c
init|=
operator|new
name|TermInfo
argument_list|(
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|TermInfo
name|d
init|=
operator|new
name|TermInfo
argument_list|(
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertConsistentEquals
argument_list|(
name|a
argument_list|,
name|a
argument_list|)
expr_stmt|;
name|assertConsistentEquals
argument_list|(
name|b
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|assertConsistentEquals
argument_list|(
name|c
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|assertConsistentEquals
argument_list|(
name|d
argument_list|,
name|d
argument_list|)
expr_stmt|;
name|assertConsistentEquals
argument_list|(
name|a
argument_list|,
name|d
argument_list|)
expr_stmt|;
name|assertConsistentLessThan
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|assertConsistentLessThan
argument_list|(
name|b
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|assertConsistentLessThan
argument_list|(
name|a
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|assertConsistentLessThan
argument_list|(
name|d
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|assertConsistentLessThan
argument_list|(
name|d
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
DECL|method|assertConsistentEquals
specifier|private
parameter_list|<
name|T
extends|extends
name|Comparable
argument_list|<
name|T
argument_list|>
parameter_list|>
name|void
name|assertConsistentEquals
parameter_list|(
name|T
name|a
parameter_list|,
name|T
name|b
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b
argument_list|,
name|a
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a
operator|.
name|hashCode
argument_list|()
argument_list|,
name|b
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|a
operator|.
name|compareTo
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|b
operator|.
name|compareTo
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertConsistentLessThan
specifier|private
parameter_list|<
name|T
extends|extends
name|Comparable
argument_list|<
name|T
argument_list|>
parameter_list|>
name|void
name|assertConsistentLessThan
parameter_list|(
name|T
name|a
parameter_list|,
name|T
name|b
parameter_list|)
block|{
name|assertFalse
argument_list|(
name|a
operator|.
name|equals
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|equals
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|a
operator|.
name|hashCode
argument_list|()
operator|==
name|b
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|a
operator|.
name|compareTo
argument_list|(
name|b
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|compareTo
argument_list|(
name|a
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
