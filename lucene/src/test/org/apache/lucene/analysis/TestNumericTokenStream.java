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
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|NumericUtils
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
name|TermToBytesRefAttribute
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
name|TypeAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|CharTermAttributeImpl
import|;
end_import
begin_class
DECL|class|TestNumericTokenStream
specifier|public
class|class
name|TestNumericTokenStream
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|lvalue
specifier|static
specifier|final
name|long
name|lvalue
init|=
literal|4573245871874382L
decl_stmt|;
DECL|field|ivalue
specifier|static
specifier|final
name|int
name|ivalue
init|=
literal|123456
decl_stmt|;
DECL|method|testLongStream
specifier|public
name|void
name|testLongStream
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|NumericTokenStream
name|stream
init|=
operator|new
name|NumericTokenStream
argument_list|()
operator|.
name|setLongValue
argument_list|(
name|lvalue
argument_list|)
decl_stmt|;
comment|// use getAttribute to test if attributes really exist, if not an IAE will be throwed
specifier|final
name|TermToBytesRefAttribute
name|bytesAtt
init|=
name|stream
operator|.
name|getAttribute
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|TypeAttribute
name|typeAtt
init|=
name|stream
operator|.
name|getAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|NumericTokenStream
operator|.
name|NumericTermAttribute
name|numericAtt
init|=
name|stream
operator|.
name|getAttribute
argument_list|(
name|NumericTokenStream
operator|.
name|NumericTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|BytesRef
name|bytes
init|=
name|bytesAtt
operator|.
name|getBytesRef
argument_list|()
decl_stmt|;
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|64
argument_list|,
name|numericAtt
operator|.
name|getValueSize
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|shift
init|=
literal|0
init|;
name|shift
operator|<
literal|64
condition|;
name|shift
operator|+=
name|NumericUtils
operator|.
name|PRECISION_STEP_DEFAULT
control|)
block|{
name|assertTrue
argument_list|(
literal|"New token is available"
argument_list|,
name|stream
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Shift value wrong"
argument_list|,
name|shift
argument_list|,
name|numericAtt
operator|.
name|getShift
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|int
name|hash
init|=
name|bytesAtt
operator|.
name|fillBytesRef
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Hash incorrect"
argument_list|,
name|bytes
operator|.
name|hashCode
argument_list|()
argument_list|,
name|hash
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Term is incorrectly encoded"
argument_list|,
name|lvalue
operator|&
operator|~
operator|(
operator|(
literal|1L
operator|<<
name|shift
operator|)
operator|-
literal|1L
operator|)
argument_list|,
name|NumericUtils
operator|.
name|prefixCodedToLong
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Term raw value is incorrectly encoded"
argument_list|,
name|lvalue
operator|&
operator|~
operator|(
operator|(
literal|1L
operator|<<
name|shift
operator|)
operator|-
literal|1L
operator|)
argument_list|,
name|numericAtt
operator|.
name|getRawValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Type incorrect"
argument_list|,
operator|(
name|shift
operator|==
literal|0
operator|)
condition|?
name|NumericTokenStream
operator|.
name|TOKEN_TYPE_FULL_PREC
else|:
name|NumericTokenStream
operator|.
name|TOKEN_TYPE_LOWER_PREC
argument_list|,
name|typeAtt
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
literal|"More tokens available"
argument_list|,
name|stream
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
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
block|}
DECL|method|testIntStream
specifier|public
name|void
name|testIntStream
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|NumericTokenStream
name|stream
init|=
operator|new
name|NumericTokenStream
argument_list|()
operator|.
name|setIntValue
argument_list|(
name|ivalue
argument_list|)
decl_stmt|;
comment|// use getAttribute to test if attributes really exist, if not an IAE will be throwed
specifier|final
name|TermToBytesRefAttribute
name|bytesAtt
init|=
name|stream
operator|.
name|getAttribute
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|TypeAttribute
name|typeAtt
init|=
name|stream
operator|.
name|getAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|NumericTokenStream
operator|.
name|NumericTermAttribute
name|numericAtt
init|=
name|stream
operator|.
name|getAttribute
argument_list|(
name|NumericTokenStream
operator|.
name|NumericTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|BytesRef
name|bytes
init|=
name|bytesAtt
operator|.
name|getBytesRef
argument_list|()
decl_stmt|;
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|32
argument_list|,
name|numericAtt
operator|.
name|getValueSize
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|shift
init|=
literal|0
init|;
name|shift
operator|<
literal|32
condition|;
name|shift
operator|+=
name|NumericUtils
operator|.
name|PRECISION_STEP_DEFAULT
control|)
block|{
name|assertTrue
argument_list|(
literal|"New token is available"
argument_list|,
name|stream
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Shift value wrong"
argument_list|,
name|shift
argument_list|,
name|numericAtt
operator|.
name|getShift
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|int
name|hash
init|=
name|bytesAtt
operator|.
name|fillBytesRef
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Hash incorrect"
argument_list|,
name|bytes
operator|.
name|hashCode
argument_list|()
argument_list|,
name|hash
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Term is incorrectly encoded"
argument_list|,
name|ivalue
operator|&
operator|~
operator|(
operator|(
literal|1
operator|<<
name|shift
operator|)
operator|-
literal|1
operator|)
argument_list|,
name|NumericUtils
operator|.
name|prefixCodedToInt
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Term raw value is incorrectly encoded"
argument_list|,
operator|(
operator|(
name|long
operator|)
name|ivalue
operator|)
operator|&
operator|~
operator|(
operator|(
literal|1L
operator|<<
name|shift
operator|)
operator|-
literal|1L
operator|)
argument_list|,
name|numericAtt
operator|.
name|getRawValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Type incorrect"
argument_list|,
operator|(
name|shift
operator|==
literal|0
operator|)
condition|?
name|NumericTokenStream
operator|.
name|TOKEN_TYPE_FULL_PREC
else|:
name|NumericTokenStream
operator|.
name|TOKEN_TYPE_LOWER_PREC
argument_list|,
name|typeAtt
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
literal|"More tokens available"
argument_list|,
name|stream
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
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
block|}
DECL|method|testNotInitialized
specifier|public
name|void
name|testNotInitialized
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|NumericTokenStream
name|stream
init|=
operator|new
name|NumericTokenStream
argument_list|()
decl_stmt|;
try|try
block|{
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"reset() should not succeed."
argument_list|)
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
try|try
block|{
name|stream
operator|.
name|incrementToken
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"incrementToken() should not succeed."
argument_list|)
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
DECL|interface|TestAttribute
specifier|public
specifier|static
interface|interface
name|TestAttribute
extends|extends
name|CharTermAttribute
block|{}
DECL|class|TestAttributeImpl
specifier|public
specifier|static
class|class
name|TestAttributeImpl
extends|extends
name|CharTermAttributeImpl
implements|implements
name|TestAttribute
block|{}
DECL|method|testCTA
specifier|public
name|void
name|testCTA
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|NumericTokenStream
name|stream
init|=
operator|new
name|NumericTokenStream
argument_list|()
decl_stmt|;
try|try
block|{
name|stream
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Succeeded to add CharTermAttribute."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|iae
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"NumericTokenStream does not support"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|stream
operator|.
name|addAttribute
argument_list|(
name|TestAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Succeeded to add TestAttribute."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|iae
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"NumericTokenStream does not support"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
