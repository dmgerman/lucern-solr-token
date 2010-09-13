begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.icu.segmentation
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|icu
operator|.
name|segmentation
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|lang
operator|.
name|UCharacter
import|;
end_import
begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|BreakIterator
import|;
end_import
begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|RuleBasedBreakIterator
import|;
end_import
begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|UTF16
import|;
end_import
begin_comment
comment|/**  * Tests LaoBreakIterator and its RBBI rules  */
end_comment
begin_class
DECL|class|TestLaoBreakIterator
specifier|public
class|class
name|TestLaoBreakIterator
extends|extends
name|LuceneTestCase
block|{
DECL|field|wordIterator
specifier|private
name|BreakIterator
name|wordIterator
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
name|InputStream
name|is
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"Lao.brk"
argument_list|)
decl_stmt|;
name|wordIterator
operator|=
operator|new
name|LaoBreakIterator
argument_list|(
name|RuleBasedBreakIterator
operator|.
name|getInstanceFromCompiledRules
argument_list|(
name|is
argument_list|)
argument_list|)
expr_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|assertBreaksTo
specifier|private
name|void
name|assertBreaksTo
parameter_list|(
name|BreakIterator
name|iterator
parameter_list|,
name|String
name|sourceText
parameter_list|,
name|String
name|tokens
index|[]
parameter_list|)
block|{
name|char
name|text
index|[]
init|=
name|sourceText
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|CharArrayIterator
name|ci
init|=
operator|new
name|CharArrayIterator
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
name|iterator
operator|.
name|setText
argument_list|(
name|ci
argument_list|)
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
name|tokens
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|start
decl_stmt|,
name|end
decl_stmt|;
do|do
block|{
name|start
operator|=
name|iterator
operator|.
name|current
argument_list|()
expr_stmt|;
name|end
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|end
operator|!=
name|BreakIterator
operator|.
name|DONE
operator|&&
operator|!
name|isWord
argument_list|(
name|text
argument_list|,
name|start
argument_list|,
name|end
argument_list|)
condition|)
do|;
name|assertTrue
argument_list|(
name|start
operator|!=
name|BreakIterator
operator|.
name|DONE
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|end
operator|!=
name|BreakIterator
operator|.
name|DONE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tokens
index|[
name|i
index|]
argument_list|,
operator|new
name|String
argument_list|(
name|text
argument_list|,
name|start
argument_list|,
name|end
operator|-
name|start
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|iterator
operator|.
name|next
argument_list|()
operator|==
name|BreakIterator
operator|.
name|DONE
argument_list|)
expr_stmt|;
block|}
DECL|method|isWord
specifier|protected
name|boolean
name|isWord
parameter_list|(
name|char
name|text
index|[]
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|int
name|codepoint
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|+=
name|UTF16
operator|.
name|getCharCount
argument_list|(
name|codepoint
argument_list|)
control|)
block|{
name|codepoint
operator|=
name|UTF16
operator|.
name|charAt
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|end
argument_list|,
name|start
argument_list|)
expr_stmt|;
if|if
condition|(
name|UCharacter
operator|.
name|isLetterOrDigit
argument_list|(
name|codepoint
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|testBasicUsage
specifier|public
name|void
name|testBasicUsage
parameter_list|()
throws|throws
name|Exception
block|{
name|assertBreaksTo
argument_list|(
name|wordIterator
argument_list|,
literal|"àºàº§à»àº²àºàº­àº"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"àºàº§à»àº²"
block|,
literal|"àºàº­àº"
block|}
argument_list|)
expr_stmt|;
name|assertBreaksTo
argument_list|(
name|wordIterator
argument_list|,
literal|"àºà»àº¹âà»àºàº»à»àº²"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"àºàº¹à»"
block|,
literal|"à»àºàº»à»àº²"
block|}
argument_list|)
expr_stmt|;
name|assertBreaksTo
argument_list|(
name|wordIterator
argument_list|,
literal|""
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
expr_stmt|;
name|assertBreaksTo
argument_list|(
name|wordIterator
argument_list|,
literal|"àºªàº°àºàº²àºàºàºµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"àºªàº°"
block|,
literal|"àºàº²àº"
block|,
literal|"àºàºµ"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testNumerics
specifier|public
name|void
name|testNumerics
parameter_list|()
throws|throws
name|Exception
block|{
name|assertBreaksTo
argument_list|(
name|wordIterator
argument_list|,
literal|"à»à»à»à»"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à»à»à»à»"
block|}
argument_list|)
expr_stmt|;
name|assertBreaksTo
argument_list|(
name|wordIterator
argument_list|,
literal|"à»à»à»à».à»à»"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à»à»à»à».à»à»"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testTextAndNumerics
specifier|public
name|void
name|testTextAndNumerics
parameter_list|()
throws|throws
name|Exception
block|{
name|assertBreaksTo
argument_list|(
name|wordIterator
argument_list|,
literal|"àºàº§à»àº²àºàº­àºà»à»à»à»"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"àºàº§à»àº²"
block|,
literal|"àºàº­àº"
block|,
literal|"à»à»à»à»"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
