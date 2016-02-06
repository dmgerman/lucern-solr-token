begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
package|;
end_package
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|BreakIterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|CharacterIterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
begin_class
DECL|class|TestCharArrayIterator
specifier|public
class|class
name|TestCharArrayIterator
extends|extends
name|LuceneTestCase
block|{
DECL|method|testWordInstance
specifier|public
name|void
name|testWordInstance
parameter_list|()
block|{
name|doTests
argument_list|(
name|CharArrayIterator
operator|.
name|newWordInstance
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testConsumeWordInstance
specifier|public
name|void
name|testConsumeWordInstance
parameter_list|()
block|{
comment|// we use the default locale, as it's randomized by LuceneTestCase
name|BreakIterator
name|bi
init|=
name|BreakIterator
operator|.
name|getWordInstance
argument_list|(
name|Locale
operator|.
name|getDefault
argument_list|()
argument_list|)
decl_stmt|;
name|CharArrayIterator
name|ci
init|=
name|CharArrayIterator
operator|.
name|newWordInstance
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
literal|10000
condition|;
name|i
operator|++
control|)
block|{
name|char
name|text
index|[]
init|=
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|ci
operator|.
name|setText
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|text
operator|.
name|length
argument_list|)
expr_stmt|;
name|consume
argument_list|(
name|bi
argument_list|,
name|ci
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* run this to test if your JRE is buggy   public void testWordInstanceJREBUG() {     // we use the default locale, as it's randomized by LuceneTestCase     BreakIterator bi = BreakIterator.getWordInstance(Locale.getDefault());     Segment ci = new Segment();     for (int i = 0; i< 10000; i++) {       char text[] = _TestUtil.randomUnicodeString(random).toCharArray();       ci.array = text;       ci.offset = 0;       ci.count = text.length;       consume(bi, ci);     }   }   */
DECL|method|testSentenceInstance
specifier|public
name|void
name|testSentenceInstance
parameter_list|()
block|{
name|doTests
argument_list|(
name|CharArrayIterator
operator|.
name|newSentenceInstance
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testConsumeSentenceInstance
specifier|public
name|void
name|testConsumeSentenceInstance
parameter_list|()
block|{
comment|// we use the default locale, as it's randomized by LuceneTestCase
name|BreakIterator
name|bi
init|=
name|BreakIterator
operator|.
name|getSentenceInstance
argument_list|(
name|Locale
operator|.
name|getDefault
argument_list|()
argument_list|)
decl_stmt|;
name|CharArrayIterator
name|ci
init|=
name|CharArrayIterator
operator|.
name|newSentenceInstance
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
literal|10000
condition|;
name|i
operator|++
control|)
block|{
name|char
name|text
index|[]
init|=
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|ci
operator|.
name|setText
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|text
operator|.
name|length
argument_list|)
expr_stmt|;
name|consume
argument_list|(
name|bi
argument_list|,
name|ci
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* run this to test if your JRE is buggy   public void testSentenceInstanceJREBUG() {     // we use the default locale, as it's randomized by LuceneTestCase     BreakIterator bi = BreakIterator.getSentenceInstance(Locale.getDefault());     Segment ci = new Segment();     for (int i = 0; i< 10000; i++) {       char text[] = _TestUtil.randomUnicodeString(random).toCharArray();       ci.array = text;       ci.offset = 0;       ci.count = text.length;       consume(bi, ci);     }   }   */
DECL|method|doTests
specifier|private
name|void
name|doTests
parameter_list|(
name|CharArrayIterator
name|ci
parameter_list|)
block|{
comment|// basics
name|ci
operator|.
name|setText
argument_list|(
literal|"testing"
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|"testing"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ci
operator|.
name|getBeginIndex
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|ci
operator|.
name|getEndIndex
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ci
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'t'
argument_list|,
name|ci
operator|.
name|current
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'e'
argument_list|,
name|ci
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'g'
argument_list|,
name|ci
operator|.
name|last
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'n'
argument_list|,
name|ci
operator|.
name|previous
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'t'
argument_list|,
name|ci
operator|.
name|first
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|CharacterIterator
operator|.
name|DONE
argument_list|,
name|ci
operator|.
name|previous
argument_list|()
argument_list|)
expr_stmt|;
comment|// first()
name|ci
operator|.
name|setText
argument_list|(
literal|"testing"
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|"testing"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|ci
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// Sets the position to getBeginIndex() and returns the character at that position.
name|assertEquals
argument_list|(
literal|'t'
argument_list|,
name|ci
operator|.
name|first
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ci
operator|.
name|getBeginIndex
argument_list|()
argument_list|,
name|ci
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
comment|// or DONE if the text is empty
name|ci
operator|.
name|setText
argument_list|(
operator|new
name|char
index|[]
block|{}
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|CharacterIterator
operator|.
name|DONE
argument_list|,
name|ci
operator|.
name|first
argument_list|()
argument_list|)
expr_stmt|;
comment|// last()
name|ci
operator|.
name|setText
argument_list|(
literal|"testing"
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|"testing"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// Sets the position to getEndIndex()-1 (getEndIndex() if the text is empty)
comment|// and returns the character at that position.
name|assertEquals
argument_list|(
literal|'g'
argument_list|,
name|ci
operator|.
name|last
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ci
operator|.
name|getIndex
argument_list|()
argument_list|,
name|ci
operator|.
name|getEndIndex
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// or DONE if the text is empty
name|ci
operator|.
name|setText
argument_list|(
operator|new
name|char
index|[]
block|{}
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|CharacterIterator
operator|.
name|DONE
argument_list|,
name|ci
operator|.
name|last
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ci
operator|.
name|getEndIndex
argument_list|()
argument_list|,
name|ci
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
comment|// current()
comment|// Gets the character at the current position (as returned by getIndex()).
name|ci
operator|.
name|setText
argument_list|(
literal|"testing"
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|"testing"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'t'
argument_list|,
name|ci
operator|.
name|current
argument_list|()
argument_list|)
expr_stmt|;
name|ci
operator|.
name|last
argument_list|()
expr_stmt|;
name|ci
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// or DONE if the current position is off the end of the text.
name|assertEquals
argument_list|(
name|CharacterIterator
operator|.
name|DONE
argument_list|,
name|ci
operator|.
name|current
argument_list|()
argument_list|)
expr_stmt|;
comment|// next()
name|ci
operator|.
name|setText
argument_list|(
literal|"te"
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|// Increments the iterator's index by one and returns the character at the new index.
name|assertEquals
argument_list|(
literal|'e'
argument_list|,
name|ci
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ci
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
comment|// or DONE if the new position is off the end of the text range.
name|assertEquals
argument_list|(
name|CharacterIterator
operator|.
name|DONE
argument_list|,
name|ci
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ci
operator|.
name|getEndIndex
argument_list|()
argument_list|,
name|ci
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
comment|// setIndex()
name|ci
operator|.
name|setText
argument_list|(
literal|"test"
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|"test"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|ci
operator|.
name|setIndex
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|instanceof
name|IllegalArgumentException
argument_list|)
expr_stmt|;
block|}
comment|// clone()
name|char
name|text
index|[]
init|=
literal|"testing"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|ci
operator|.
name|setText
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|text
operator|.
name|length
argument_list|)
expr_stmt|;
name|ci
operator|.
name|next
argument_list|()
expr_stmt|;
name|CharArrayIterator
name|ci2
init|=
name|ci
operator|.
name|clone
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|ci
operator|.
name|getIndex
argument_list|()
argument_list|,
name|ci2
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ci
operator|.
name|next
argument_list|()
argument_list|,
name|ci2
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ci
operator|.
name|last
argument_list|()
argument_list|,
name|ci2
operator|.
name|last
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|consume
specifier|private
name|void
name|consume
parameter_list|(
name|BreakIterator
name|bi
parameter_list|,
name|CharacterIterator
name|ci
parameter_list|)
block|{
name|bi
operator|.
name|setText
argument_list|(
name|ci
argument_list|)
expr_stmt|;
while|while
condition|(
name|bi
operator|.
name|next
argument_list|()
operator|!=
name|BreakIterator
operator|.
name|DONE
condition|)
empty_stmt|;
block|}
block|}
end_class
end_unit
