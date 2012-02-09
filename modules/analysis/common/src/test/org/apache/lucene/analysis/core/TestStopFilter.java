begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.core
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|core
package|;
end_package
begin_comment
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|util
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
name|util
operator|.
name|English
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
name|Version
import|;
end_import
begin_class
DECL|class|TestStopFilter
specifier|public
class|class
name|TestStopFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|// other StopFilter functionality is already tested by TestStopAnalyzer
DECL|method|testExactCase
specifier|public
name|void
name|testExactCase
parameter_list|()
throws|throws
name|IOException
block|{
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"Now is The Time"
argument_list|)
decl_stmt|;
name|CharArraySet
name|stopWords
init|=
operator|new
name|CharArraySet
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|asSet
argument_list|(
literal|"is"
argument_list|,
literal|"the"
argument_list|,
literal|"Time"
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
operator|new
name|StopFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|,
name|stopWords
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Now"
block|,
literal|"The"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testStopFilt
specifier|public
name|void
name|testStopFilt
parameter_list|()
throws|throws
name|IOException
block|{
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"Now is The Time"
argument_list|)
decl_stmt|;
name|String
index|[]
name|stopWords
init|=
operator|new
name|String
index|[]
block|{
literal|"is"
block|,
literal|"the"
block|,
literal|"Time"
block|}
decl_stmt|;
name|CharArraySet
name|stopSet
init|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|stopWords
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
operator|new
name|StopFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|,
name|stopSet
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Now"
block|,
literal|"The"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test Position increments applied by StopFilter with and without enabling this option.    */
DECL|method|testStopPositons
specifier|public
name|void
name|testStopPositons
parameter_list|()
throws|throws
name|IOException
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|a
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
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
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|String
name|w
init|=
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|w
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|%
literal|3
operator|!=
literal|0
condition|)
name|a
operator|.
name|add
argument_list|(
name|w
argument_list|)
expr_stmt|;
block|}
name|log
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|stopWords
index|[]
init|=
name|a
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
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
name|a
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
name|log
argument_list|(
literal|"Stop: "
operator|+
name|stopWords
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|CharArraySet
name|stopSet
init|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|stopWords
argument_list|)
decl_stmt|;
comment|// with increments
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|StopFilter
name|stpf
init|=
operator|new
name|StopFilter
argument_list|(
name|Version
operator|.
name|LUCENE_40
argument_list|,
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|,
name|stopSet
argument_list|)
decl_stmt|;
name|doTestStopPositons
argument_list|(
name|stpf
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// without increments
name|reader
operator|=
operator|new
name|StringReader
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|stpf
operator|=
operator|new
name|StopFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|,
name|stopSet
argument_list|)
expr_stmt|;
name|doTestStopPositons
argument_list|(
name|stpf
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// with increments, concatenating two stop filters
name|ArrayList
argument_list|<
name|String
argument_list|>
name|a0
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|a1
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
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
name|a
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|a0
operator|.
name|add
argument_list|(
name|a
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|a1
operator|.
name|add
argument_list|(
name|a
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|stopWords0
index|[]
init|=
name|a0
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
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
name|a0
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
name|log
argument_list|(
literal|"Stop0: "
operator|+
name|stopWords0
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|String
name|stopWords1
index|[]
init|=
name|a1
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
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
name|a1
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
name|log
argument_list|(
literal|"Stop1: "
operator|+
name|stopWords1
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|CharArraySet
name|stopSet0
init|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|stopWords0
argument_list|)
decl_stmt|;
name|CharArraySet
name|stopSet1
init|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|stopWords1
argument_list|)
decl_stmt|;
name|reader
operator|=
operator|new
name|StringReader
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|StopFilter
name|stpf0
init|=
operator|new
name|StopFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|,
name|stopSet0
argument_list|)
decl_stmt|;
comment|// first part of the set
name|stpf0
operator|.
name|setEnablePositionIncrements
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|StopFilter
name|stpf01
init|=
operator|new
name|StopFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|stpf0
argument_list|,
name|stopSet1
argument_list|)
decl_stmt|;
comment|// two stop filters concatenated!
name|doTestStopPositons
argument_list|(
name|stpf01
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestStopPositons
specifier|private
name|void
name|doTestStopPositons
parameter_list|(
name|StopFilter
name|stpf
parameter_list|,
name|boolean
name|enableIcrements
parameter_list|)
throws|throws
name|IOException
block|{
name|log
argument_list|(
literal|"---> test with enable-increments-"
operator|+
operator|(
name|enableIcrements
condition|?
literal|"enabled"
else|:
literal|"disabled"
operator|)
argument_list|)
expr_stmt|;
name|stpf
operator|.
name|setEnablePositionIncrements
argument_list|(
name|enableIcrements
argument_list|)
expr_stmt|;
name|CharTermAttribute
name|termAtt
init|=
name|stpf
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|PositionIncrementAttribute
name|posIncrAtt
init|=
name|stpf
operator|.
name|getAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|stpf
operator|.
name|reset
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|20
condition|;
name|i
operator|+=
literal|3
control|)
block|{
name|assertTrue
argument_list|(
name|stpf
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|log
argument_list|(
literal|"Token "
operator|+
name|i
operator|+
literal|": "
operator|+
name|stpf
argument_list|)
expr_stmt|;
name|String
name|w
init|=
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"expecting token "
operator|+
name|i
operator|+
literal|" to be "
operator|+
name|w
argument_list|,
name|w
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"all but first token must have position increment of 3"
argument_list|,
name|enableIcrements
condition|?
operator|(
name|i
operator|==
literal|0
condition|?
literal|1
else|:
literal|3
operator|)
else|:
literal|1
argument_list|,
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|stpf
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|stpf
operator|.
name|end
argument_list|()
expr_stmt|;
name|stpf
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// print debug info depending on VERBOSE
DECL|method|log
specifier|private
specifier|static
name|void
name|log
parameter_list|(
name|String
name|s
parameter_list|)
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
name|s
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
