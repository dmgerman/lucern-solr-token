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
name|util
operator|.
name|CharArraySet
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
name|Iterator
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
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_class
DECL|class|TestStopAnalyzer
specifier|public
class|class
name|TestStopAnalyzer
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|stop
specifier|private
name|StopAnalyzer
name|stop
decl_stmt|;
DECL|field|inValidTokens
specifier|private
name|Set
argument_list|<
name|Object
argument_list|>
name|inValidTokens
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
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
name|Iterator
argument_list|<
name|?
argument_list|>
name|it
init|=
name|StopAnalyzer
operator|.
name|ENGLISH_STOP_WORDS_SET
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|inValidTokens
operator|.
name|add
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|stop
operator|=
operator|new
name|StopAnalyzer
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
name|stop
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
DECL|method|testDefaults
specifier|public
name|void
name|testDefaults
parameter_list|()
throws|throws
name|IOException
block|{
name|assertTrue
argument_list|(
name|stop
operator|!=
literal|null
argument_list|)
expr_stmt|;
try|try
init|(
name|TokenStream
name|stream
init|=
name|stop
operator|.
name|tokenStream
argument_list|(
literal|"test"
argument_list|,
literal|"This is a test of the english stop analyzer"
argument_list|)
init|)
block|{
name|assertTrue
argument_list|(
name|stream
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|CharTermAttribute
name|termAtt
init|=
name|stream
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
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
name|assertFalse
argument_list|(
name|inValidTokens
operator|.
name|contains
argument_list|(
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|stream
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testStopList
specifier|public
name|void
name|testStopList
parameter_list|()
throws|throws
name|IOException
block|{
name|CharArraySet
name|stopWordsSet
init|=
operator|new
name|CharArraySet
argument_list|(
name|asSet
argument_list|(
literal|"good"
argument_list|,
literal|"test"
argument_list|,
literal|"analyzer"
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|StopAnalyzer
name|newStop
init|=
operator|new
name|StopAnalyzer
argument_list|(
name|stopWordsSet
argument_list|)
decl_stmt|;
try|try
init|(
name|TokenStream
name|stream
init|=
name|newStop
operator|.
name|tokenStream
argument_list|(
literal|"test"
argument_list|,
literal|"This is a good test of the english stop analyzer"
argument_list|)
init|)
block|{
name|assertNotNull
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|CharTermAttribute
name|termAtt
init|=
name|stream
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
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
name|String
name|text
init|=
name|termAtt
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|stopWordsSet
operator|.
name|contains
argument_list|(
name|text
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|stream
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
name|newStop
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testStopListPositions
specifier|public
name|void
name|testStopListPositions
parameter_list|()
throws|throws
name|IOException
block|{
name|CharArraySet
name|stopWordsSet
init|=
operator|new
name|CharArraySet
argument_list|(
name|asSet
argument_list|(
literal|"good"
argument_list|,
literal|"test"
argument_list|,
literal|"analyzer"
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|StopAnalyzer
name|newStop
init|=
operator|new
name|StopAnalyzer
argument_list|(
name|stopWordsSet
argument_list|)
decl_stmt|;
name|String
name|s
init|=
literal|"This is a good test of the english stop analyzer with positions"
decl_stmt|;
name|int
name|expectedIncr
index|[]
init|=
block|{
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|3
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|2
block|,
literal|1
block|}
decl_stmt|;
try|try
init|(
name|TokenStream
name|stream
init|=
name|newStop
operator|.
name|tokenStream
argument_list|(
literal|"test"
argument_list|,
name|s
argument_list|)
init|)
block|{
name|assertNotNull
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
name|CharTermAttribute
name|termAtt
init|=
name|stream
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
name|stream
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
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
name|String
name|text
init|=
name|termAtt
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|stopWordsSet
operator|.
name|contains
argument_list|(
name|text
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedIncr
index|[
name|i
operator|++
index|]
argument_list|,
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|stream
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
name|newStop
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
