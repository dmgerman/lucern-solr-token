begin_unit
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
DECL|class|TestJavascriptFunction
specifier|public
class|class
name|TestJavascriptFunction
extends|extends
name|LuceneTestCase
block|{
DECL|field|DELTA
specifier|private
specifier|static
name|double
name|DELTA
init|=
literal|0.0000001
decl_stmt|;
DECL|method|assertEvaluatesTo
specifier|private
name|void
name|assertEvaluatesTo
parameter_list|(
name|String
name|expression
parameter_list|,
name|double
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
name|double
name|actual
init|=
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
argument_list|,
name|DELTA
argument_list|)
expr_stmt|;
block|}
DECL|method|testAbsMethod
specifier|public
name|void
name|testAbsMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"abs(0)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"abs(119)"
argument_list|,
literal|119
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"abs(119)"
argument_list|,
literal|119
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"abs(1)"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"abs(-1)"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testAcosMethod
specifier|public
name|void
name|testAcosMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"acos(-1)"
argument_list|,
name|Math
operator|.
name|PI
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"acos(-0.8660254)"
argument_list|,
name|Math
operator|.
name|PI
operator|*
literal|5
operator|/
literal|6
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"acos(-0.7071068)"
argument_list|,
name|Math
operator|.
name|PI
operator|*
literal|3
operator|/
literal|4
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"acos(-0.5)"
argument_list|,
name|Math
operator|.
name|PI
operator|*
literal|2
operator|/
literal|3
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"acos(0)"
argument_list|,
name|Math
operator|.
name|PI
operator|/
literal|2
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"acos(0.5)"
argument_list|,
name|Math
operator|.
name|PI
operator|/
literal|3
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"acos(0.7071068)"
argument_list|,
name|Math
operator|.
name|PI
operator|/
literal|4
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"acos(0.8660254)"
argument_list|,
name|Math
operator|.
name|PI
operator|/
literal|6
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"acos(1)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testAcoshMethod
specifier|public
name|void
name|testAcoshMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"acosh(1)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"acosh(2.5)"
argument_list|,
literal|1.5667992369724109
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"acosh(1234567.89)"
argument_list|,
literal|14.719378760739708
argument_list|)
expr_stmt|;
block|}
DECL|method|testAsinMethod
specifier|public
name|void
name|testAsinMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"asin(-1)"
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|/
literal|2
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"asin(-0.8660254)"
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|/
literal|3
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"asin(-0.7071068)"
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|/
literal|4
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"asin(-0.5)"
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|/
literal|6
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"asin(0)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"asin(0.5)"
argument_list|,
name|Math
operator|.
name|PI
operator|/
literal|6
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"asin(0.7071068)"
argument_list|,
name|Math
operator|.
name|PI
operator|/
literal|4
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"asin(0.8660254)"
argument_list|,
name|Math
operator|.
name|PI
operator|/
literal|3
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"asin(1)"
argument_list|,
name|Math
operator|.
name|PI
operator|/
literal|2
argument_list|)
expr_stmt|;
block|}
DECL|method|testAsinhMethod
specifier|public
name|void
name|testAsinhMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"asinh(-1234567.89)"
argument_list|,
operator|-
literal|14.719378760740035
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"asinh(-2.5)"
argument_list|,
operator|-
literal|1.6472311463710958
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"asinh(-1)"
argument_list|,
operator|-
literal|0.8813735870195429
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"asinh(0)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"asinh(1)"
argument_list|,
literal|0.8813735870195429
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"asinh(2.5)"
argument_list|,
literal|1.6472311463710958
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"asinh(1234567.89)"
argument_list|,
literal|14.719378760740035
argument_list|)
expr_stmt|;
block|}
DECL|method|testAtanMethod
specifier|public
name|void
name|testAtanMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"atan(-1.732050808)"
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|/
literal|3
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"atan(-1)"
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|/
literal|4
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"atan(-0.577350269)"
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|/
literal|6
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"atan(0)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"atan(0.577350269)"
argument_list|,
name|Math
operator|.
name|PI
operator|/
literal|6
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"atan(1)"
argument_list|,
name|Math
operator|.
name|PI
operator|/
literal|4
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"atan(1.732050808)"
argument_list|,
name|Math
operator|.
name|PI
operator|/
literal|3
argument_list|)
expr_stmt|;
block|}
DECL|method|testAtan2Method
specifier|public
name|void
name|testAtan2Method
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"atan2(+0,+0)"
argument_list|,
operator|+
literal|0.0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"atan2(+0,-0)"
argument_list|,
operator|+
name|Math
operator|.
name|PI
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"atan2(-0,+0)"
argument_list|,
operator|-
literal|0.0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"atan2(-0,-0)"
argument_list|,
operator|-
name|Math
operator|.
name|PI
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"atan2(2,2)"
argument_list|,
name|Math
operator|.
name|PI
operator|/
literal|4
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"atan2(-2,2)"
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|/
literal|4
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"atan2(2,-2)"
argument_list|,
name|Math
operator|.
name|PI
operator|*
literal|3
operator|/
literal|4
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"atan2(-2,-2)"
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|*
literal|3
operator|/
literal|4
argument_list|)
expr_stmt|;
block|}
DECL|method|testAtanhMethod
specifier|public
name|void
name|testAtanhMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"atanh(-1)"
argument_list|,
name|Double
operator|.
name|NEGATIVE_INFINITY
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"atanh(-0.5)"
argument_list|,
operator|-
literal|0.5493061443340549
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"atanh(0)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"atanh(0.5)"
argument_list|,
literal|0.5493061443340549
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"atanh(1)"
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
expr_stmt|;
block|}
DECL|method|testCeilMethod
specifier|public
name|void
name|testCeilMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"ceil(0)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"ceil(0.1)"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"ceil(0.9)"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"ceil(25.2)"
argument_list|,
literal|26
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"ceil(-0.1)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"ceil(-0.9)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"ceil(-1.1)"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testCosMethod
specifier|public
name|void
name|testCosMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"cos(0)"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"cos("
operator|+
name|Math
operator|.
name|PI
operator|/
literal|2
operator|+
literal|")"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"cos("
operator|+
operator|-
name|Math
operator|.
name|PI
operator|/
literal|2
operator|+
literal|")"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"cos("
operator|+
name|Math
operator|.
name|PI
operator|/
literal|4
operator|+
literal|")"
argument_list|,
literal|0.7071068
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"cos("
operator|+
operator|-
name|Math
operator|.
name|PI
operator|/
literal|4
operator|+
literal|")"
argument_list|,
literal|0.7071068
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"cos("
operator|+
name|Math
operator|.
name|PI
operator|*
literal|2
operator|/
literal|3
operator|+
literal|")"
argument_list|,
operator|-
literal|0.5
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"cos("
operator|+
operator|-
name|Math
operator|.
name|PI
operator|*
literal|2
operator|/
literal|3
operator|+
literal|")"
argument_list|,
operator|-
literal|0.5
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"cos("
operator|+
name|Math
operator|.
name|PI
operator|/
literal|6
operator|+
literal|")"
argument_list|,
literal|0.8660254
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"cos("
operator|+
operator|-
name|Math
operator|.
name|PI
operator|/
literal|6
operator|+
literal|")"
argument_list|,
literal|0.8660254
argument_list|)
expr_stmt|;
block|}
DECL|method|testCoshMethod
specifier|public
name|void
name|testCoshMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"cosh(0)"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"cosh(-1)"
argument_list|,
literal|1.5430806348152437
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"cosh(1)"
argument_list|,
literal|1.5430806348152437
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"cosh(-0.5)"
argument_list|,
literal|1.1276259652063807
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"cosh(0.5)"
argument_list|,
literal|1.1276259652063807
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"cosh(-12.3456789)"
argument_list|,
literal|114982.09728671524
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"cosh(12.3456789)"
argument_list|,
literal|114982.09728671524
argument_list|)
expr_stmt|;
block|}
DECL|method|testExpMethod
specifier|public
name|void
name|testExpMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"exp(0)"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"exp(-1)"
argument_list|,
literal|0.36787944117
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"exp(1)"
argument_list|,
literal|2.71828182846
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"exp(-0.5)"
argument_list|,
literal|0.60653065971
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"exp(0.5)"
argument_list|,
literal|1.6487212707
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"exp(-12.3456789)"
argument_list|,
literal|0.0000043485
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"exp(12.3456789)"
argument_list|,
literal|229964.194569
argument_list|)
expr_stmt|;
block|}
DECL|method|testFloorMethod
specifier|public
name|void
name|testFloorMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"floor(0)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"floor(0.1)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"floor(0.9)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"floor(25.2)"
argument_list|,
literal|25
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"floor(-0.1)"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"floor(-0.9)"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"floor(-1.1)"
argument_list|,
operator|-
literal|2
argument_list|)
expr_stmt|;
block|}
DECL|method|testLnMethod
specifier|public
name|void
name|testLnMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"ln(0)"
argument_list|,
name|Double
operator|.
name|NEGATIVE_INFINITY
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"ln("
operator|+
name|Math
operator|.
name|E
operator|+
literal|")"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"ln(-1)"
argument_list|,
name|Double
operator|.
name|NaN
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"ln(1)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"ln(0.5)"
argument_list|,
operator|-
literal|0.69314718056
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"ln(12.3456789)"
argument_list|,
literal|2.51330611521
argument_list|)
expr_stmt|;
block|}
DECL|method|testLog10Method
specifier|public
name|void
name|testLog10Method
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"log10(0)"
argument_list|,
name|Double
operator|.
name|NEGATIVE_INFINITY
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"log10(1)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"log10(-1)"
argument_list|,
name|Double
operator|.
name|NaN
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"log10(0.5)"
argument_list|,
operator|-
literal|0.3010299956639812
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"log10(12.3456789)"
argument_list|,
literal|1.0915149771692705
argument_list|)
expr_stmt|;
block|}
DECL|method|testLognMethod
specifier|public
name|void
name|testLognMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"logn(2, 0)"
argument_list|,
name|Double
operator|.
name|NEGATIVE_INFINITY
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"logn(2, 1)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"logn(2, -1)"
argument_list|,
name|Double
operator|.
name|NaN
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"logn(2, 0.5)"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"logn(2, 12.3456789)"
argument_list|,
literal|3.6259342686489378
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"logn(2.5, 0)"
argument_list|,
name|Double
operator|.
name|NEGATIVE_INFINITY
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"logn(2.5, 1)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"logn(2.5, -1)"
argument_list|,
name|Double
operator|.
name|NaN
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"logn(2.5, 0.5)"
argument_list|,
operator|-
literal|0.75647079736603
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"logn(2.5, 12.3456789)"
argument_list|,
literal|2.7429133874016745
argument_list|)
expr_stmt|;
block|}
DECL|method|testMaxMethod
specifier|public
name|void
name|testMaxMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"max(0, 0)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"max(1, 0)"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"max(0, -1)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"max(-1, 0)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"max(25, 23)"
argument_list|,
literal|25
argument_list|)
expr_stmt|;
block|}
DECL|method|testMinMethod
specifier|public
name|void
name|testMinMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"min(0, 0)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"min(1, 0)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"min(0, -1)"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"min(-1, 0)"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"min(25, 23)"
argument_list|,
literal|23
argument_list|)
expr_stmt|;
block|}
DECL|method|testPowMethod
specifier|public
name|void
name|testPowMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"pow(0, 0)"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"pow(0.1, 2)"
argument_list|,
literal|0.01
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"pow(0.9, -1)"
argument_list|,
literal|1.1111111111111112
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"pow(2.2, -2.5)"
argument_list|,
literal|0.13929749224447147
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"pow(5, 3)"
argument_list|,
literal|125
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"pow(-0.9, 5)"
argument_list|,
operator|-
literal|0.59049
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"pow(-1.1, 2)"
argument_list|,
literal|1.21
argument_list|)
expr_stmt|;
block|}
DECL|method|testSinMethod
specifier|public
name|void
name|testSinMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"sin(0)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"sin("
operator|+
name|Math
operator|.
name|PI
operator|/
literal|2
operator|+
literal|")"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"sin("
operator|+
operator|-
name|Math
operator|.
name|PI
operator|/
literal|2
operator|+
literal|")"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"sin("
operator|+
name|Math
operator|.
name|PI
operator|/
literal|4
operator|+
literal|")"
argument_list|,
literal|0.7071068
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"sin("
operator|+
operator|-
name|Math
operator|.
name|PI
operator|/
literal|4
operator|+
literal|")"
argument_list|,
operator|-
literal|0.7071068
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"sin("
operator|+
name|Math
operator|.
name|PI
operator|*
literal|2
operator|/
literal|3
operator|+
literal|")"
argument_list|,
literal|0.8660254
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"sin("
operator|+
operator|-
name|Math
operator|.
name|PI
operator|*
literal|2
operator|/
literal|3
operator|+
literal|")"
argument_list|,
operator|-
literal|0.8660254
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"sin("
operator|+
name|Math
operator|.
name|PI
operator|/
literal|6
operator|+
literal|")"
argument_list|,
literal|0.5
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"sin("
operator|+
operator|-
name|Math
operator|.
name|PI
operator|/
literal|6
operator|+
literal|")"
argument_list|,
operator|-
literal|0.5
argument_list|)
expr_stmt|;
block|}
DECL|method|testSinhMethod
specifier|public
name|void
name|testSinhMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"sinh(0)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"sinh(-1)"
argument_list|,
operator|-
literal|1.1752011936438014
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"sinh(1)"
argument_list|,
literal|1.1752011936438014
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"sinh(-0.5)"
argument_list|,
operator|-
literal|0.52109530549
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"sinh(0.5)"
argument_list|,
literal|0.52109530549
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"sinh(-12.3456789)"
argument_list|,
operator|-
literal|114982.09728236674
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"sinh(12.3456789)"
argument_list|,
literal|114982.09728236674
argument_list|)
expr_stmt|;
block|}
DECL|method|testSqrtMethod
specifier|public
name|void
name|testSqrtMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"sqrt(0)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"sqrt(-1)"
argument_list|,
name|Double
operator|.
name|NaN
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"sqrt(0.49)"
argument_list|,
literal|0.7
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"sqrt(49)"
argument_list|,
literal|7
argument_list|)
expr_stmt|;
block|}
DECL|method|testTanMethod
specifier|public
name|void
name|testTanMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"tan(0)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"tan(-1)"
argument_list|,
operator|-
literal|1.55740772465
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"tan(1)"
argument_list|,
literal|1.55740772465
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"tan(-0.5)"
argument_list|,
operator|-
literal|0.54630248984
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"tan(0.5)"
argument_list|,
literal|0.54630248984
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"tan(-1.3)"
argument_list|,
operator|-
literal|3.60210244797
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"tan(1.3)"
argument_list|,
literal|3.60210244797
argument_list|)
expr_stmt|;
block|}
DECL|method|testTanhMethod
specifier|public
name|void
name|testTanhMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEvaluatesTo
argument_list|(
literal|"tanh(0)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"tanh(-1)"
argument_list|,
operator|-
literal|0.76159415595
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"tanh(1)"
argument_list|,
literal|0.76159415595
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"tanh(-0.5)"
argument_list|,
operator|-
literal|0.46211715726
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"tanh(0.5)"
argument_list|,
literal|0.46211715726
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"tanh(-12.3456789)"
argument_list|,
operator|-
literal|0.99999999996
argument_list|)
expr_stmt|;
name|assertEvaluatesTo
argument_list|(
literal|"tanh(12.3456789)"
argument_list|,
literal|0.99999999996
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
