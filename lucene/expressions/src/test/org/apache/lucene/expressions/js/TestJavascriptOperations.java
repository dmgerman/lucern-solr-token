begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.expressions.js
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|expressions
operator|.
name|js
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
name|expressions
operator|.
name|Expression
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
name|LuceneTestCase
import|;
end_import
begin_class
DECL|class|TestJavascriptOperations
specifier|public
class|class
name|TestJavascriptOperations
extends|extends
name|LuceneTestCase
block|{
DECL|method|assertEvaluatesTo
specifier|private
name|void
name|assertEvaluatesTo
parameter_list|(
name|String
name|expression
parameter_list|,
name|long
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
name|Expression
name|evaluator
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
name|expression
argument_list|)
decl_stmt|;
name|long
name|actual
init|=
operator|(
name|long
operator|)
name|evaluator
operator|.
name|evaluate
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
DECL|method|testNegationOperation
specifier|public
name|void
name|testNegationOperation
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"-1"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"--1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-(-1)"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-0"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"--0"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testAddOperation
specifier|public
name|void
name|testAddOperation
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"1+1"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1+0.5+0.5"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"5+10"
argument_list|,
literal|15
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1+1+2"
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"(1+1)+2"
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1+(1+2)"
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0+1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1+0"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0+0"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testSubtractOperation
specifier|public
name|void
name|testSubtractOperation
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"1-1"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"5-10"
argument_list|,
operator|-
literal|5
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1-0.5-0.5"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1-1-2"
argument_list|,
operator|-
literal|2
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"(1-1)-2"
argument_list|,
operator|-
literal|2
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1-(1-2)"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0-1"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1-0"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0-0"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultiplyOperation
specifier|public
name|void
name|testMultiplyOperation
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"1*1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"5*10"
argument_list|,
literal|50
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"50*0.1"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1*1*2"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"(1*1)*2"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1*(1*2)"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"10*0"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0*0"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testDivisionOperation
specifier|public
name|void
name|testDivisionOperation
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"1*1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"10/5"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"10/0.5"
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"10/5/2"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"(27/9)/3"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"27/(9/3)"
argument_list|,
literal|9
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1/0"
argument_list|,
literal|9223372036854775807L
argument_list|)
expr_stmt|;
block|}
DECL|method|testModuloOperation
specifier|public
name|void
name|testModuloOperation
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"1%1"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"10%3"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"10%3%2"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"(27%10)%4"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"27%(9%5)"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
DECL|method|testLessThanOperation
specifier|public
name|void
name|testLessThanOperation
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"1< 1"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"2< 1"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1< 2"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"2< 1< 3"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"2< (1< 3)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"(2< 1)< 1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1< -2"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1< 0"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testLessThanEqualsOperation
specifier|public
name|void
name|testLessThanEqualsOperation
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"1<= 1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"2<= 1"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1<= 2"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1<= 1<= 0"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1<= -1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1<= 0"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1<= -2"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1<= 0"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testGreaterThanOperation
specifier|public
name|void
name|testGreaterThanOperation
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"1> 1"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"2> 1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1> 2"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"2> 1> 3"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"2> (1> 3)"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"(2> 1)> 1"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1> -2"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1> 0"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testGreaterThanEqualsOperation
specifier|public
name|void
name|testGreaterThanEqualsOperation
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"1>= 1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"2>= 1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1>= 2"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1>= 1>= 0"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1>= -1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1>= 0"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1>= -2"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1>= 0"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testEqualsOperation
specifier|public
name|void
name|testEqualsOperation
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"1 == 1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0 == 0"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1 == -1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1.1 == 1.1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0.9 == 0.9"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-0 == 0"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0 == 1"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1 == 2"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1 == 1"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1 == 0"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-2 == 1"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-2 == -1"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testNotEqualsOperation
specifier|public
name|void
name|testNotEqualsOperation
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"1 != 1"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0 != 0"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1 != -1"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1.1 != 1.1"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0.9 != 0.9"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-0 != 0"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0 != 1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1 != 2"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1 != 1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1 != 0"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-2 != 1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-2 != -1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testBoolNotOperation
specifier|public
name|void
name|testBoolNotOperation
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"!1"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"!!1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"!0"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"!!0"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"!-1"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"!2"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"!-2"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testBoolAndOperation
specifier|public
name|void
name|testBoolAndOperation
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"1&& 1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1&& 0"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0&& 1"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0&& 0"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1&& -1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1&& 0"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0&& -1"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-0&& -0"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testBoolOrOperation
specifier|public
name|void
name|testBoolOrOperation
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"1 || 1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1 || 0"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0 || 1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0 || 0"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1 || -1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1 || 0"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0 || -1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-0 || -0"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testConditionalOperation
specifier|public
name|void
name|testConditionalOperation
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"1 ? 2 : 3"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1 ? 2 : 3"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0 ? 2 : 3"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1 ? 2 ? 3 : 4 : 5"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0 ? 2 ? 3 : 4 : 5"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1 ? 0 ? 3 : 4 : 5"
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1 ? 2 : 3 ? 4 : 5"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0 ? 2 : 3 ? 4 : 5"
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0 ? 2 : 0 ? 4 : 5"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"(1 ? 1 : 0) ? 3 : 4"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"(0 ? 1 : 0) ? 3 : 4"
argument_list|,
literal|4
argument_list|)
expr_stmt|;
block|}
DECL|method|testBitShiftLeft
specifier|public
name|void
name|testBitShiftLeft
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"1<< 1"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"2<< 1"
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1<< 31"
argument_list|,
operator|-
literal|2147483648
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"3<< 5"
argument_list|,
literal|96
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-5<< 3"
argument_list|,
operator|-
literal|40
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"4195<< 7"
argument_list|,
literal|536960
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"4195<< 66"
argument_list|,
literal|16780
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"4195<< 6"
argument_list|,
literal|268480
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"4195<< 70"
argument_list|,
literal|268480
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-4195<< 70"
argument_list|,
operator|-
literal|268480
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-15<< 62"
argument_list|,
literal|4611686018427387904L
argument_list|)
expr_stmt|;
block|}
DECL|method|testBitShiftRight
specifier|public
name|void
name|testBitShiftRight
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"1>> 1"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"2>> 1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1>> 5"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-2>> 30"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-5>> 1"
argument_list|,
operator|-
literal|3
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"536960>> 7"
argument_list|,
literal|4195
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"16780>> 66"
argument_list|,
literal|4195
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"268480>> 6"
argument_list|,
literal|4195
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"268480>> 70"
argument_list|,
literal|4195
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-268480>> 70"
argument_list|,
operator|-
literal|4195
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-2147483646>> 1"
argument_list|,
operator|-
literal|1073741823
argument_list|)
expr_stmt|;
block|}
DECL|method|testBitShiftRightUnsigned
specifier|public
name|void
name|testBitShiftRightUnsigned
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"1>>> 1"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"2>>> 1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1>>> 37"
argument_list|,
literal|134217727
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-2>>> 62"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-5>>> 33"
argument_list|,
literal|2147483647
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"536960>>> 7"
argument_list|,
literal|4195
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"16780>>> 66"
argument_list|,
literal|4195
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"268480>>> 6"
argument_list|,
literal|4195
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"268480>>> 70"
argument_list|,
literal|4195
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-268480>>> 102"
argument_list|,
literal|67108863
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"2147483648>>> 1"
argument_list|,
literal|1073741824
argument_list|)
expr_stmt|;
block|}
DECL|method|testBitwiseAnd
specifier|public
name|void
name|testBitwiseAnd
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"4& 4"
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"3& 2"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"7& 3"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1& -1"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1& 25"
argument_list|,
literal|25
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"3& 7"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0& 1"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1& 0"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testBitwiseOr
specifier|public
name|void
name|testBitwiseOr
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"4 | 4"
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"5 | 2"
argument_list|,
literal|7
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"7 | 3"
argument_list|,
literal|7
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1 | -5"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1 | 25"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-100 | 15"
argument_list|,
operator|-
literal|97
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0 | 1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1 | 0"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testBitwiseXor
specifier|public
name|void
name|testBitwiseXor
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"4 ^ 4"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"5 ^ 2"
argument_list|,
literal|7
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"15 ^ 3"
argument_list|,
literal|12
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1 ^ -5"
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-1 ^ 25"
argument_list|,
operator|-
literal|26
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"-100 ^ 15"
argument_list|,
operator|-
literal|109
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0 ^ 1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1 ^ 0"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0 ^ 0"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testBitwiseNot
specifier|public
name|void
name|testBitwiseNot
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"~-5"
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"~25"
argument_list|,
operator|-
literal|26
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"~0"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"~-1"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testDecimalConst
specifier|public
name|void
name|testDecimalConst
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"0"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"123456789"
argument_list|,
literal|123456789
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"5.6E2"
argument_list|,
literal|560
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"5.6E+2"
argument_list|,
literal|560
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"500E-2"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testHexConst
specifier|public
name|void
name|testHexConst
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"0x0"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0x1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0xF"
argument_list|,
literal|15
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0x1234ABCDEF"
argument_list|,
literal|78193085935L
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1<< 0x1"
argument_list|,
literal|1
operator|<<
literal|0x1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1<< 0xA"
argument_list|,
literal|1
operator|<<
literal|0xA
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0x1<< 2"
argument_list|,
literal|0x1
operator|<<
literal|2
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0xA<< 2"
argument_list|,
literal|0xA
operator|<<
literal|2
argument_list|)
expr_stmt|;
block|}
DECL|method|testHexConst2
specifier|public
name|void
name|testHexConst2
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"0X0"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0X1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0XF"
argument_list|,
literal|15
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0X1234ABCDEF"
argument_list|,
literal|78193085935L
argument_list|)
expr_stmt|;
block|}
DECL|method|testOctalConst
specifier|public
name|void
name|testOctalConst
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"00"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"01"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"010"
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"0123456777"
argument_list|,
literal|21913087
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1<< 01"
argument_list|,
literal|1
operator|<<
literal|01
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"1<< 010"
argument_list|,
literal|1
operator|<<
literal|010
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"01<< 2"
argument_list|,
literal|01
operator|<<
literal|2
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"010<< 2"
argument_list|,
literal|010
operator|<<
literal|2
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
