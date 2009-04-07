begin_unit
begin_package
DECL|package|org.apache.lucene.search.trie
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|trie
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
name|tokenattributes
operator|.
name|TermAttribute
import|;
end_import
begin_class
DECL|class|TestLongTrieTokenStream
specifier|public
class|class
name|TestLongTrieTokenStream
extends|extends
name|LuceneTestCase
block|{
DECL|field|precisionStep
specifier|static
specifier|final
name|int
name|precisionStep
init|=
literal|8
decl_stmt|;
DECL|field|value
specifier|static
specifier|final
name|long
name|value
init|=
literal|4573245871874382L
decl_stmt|;
DECL|method|testStreamNewAPI
specifier|public
name|void
name|testStreamNewAPI
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|LongTrieTokenStream
name|stream
init|=
operator|new
name|LongTrieTokenStream
argument_list|(
name|value
argument_list|,
name|precisionStep
argument_list|)
decl_stmt|;
name|stream
operator|.
name|setUseNewAPI
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|ShiftAttribute
name|shiftAtt
init|=
operator|(
name|ShiftAttribute
operator|)
name|stream
operator|.
name|addAttribute
argument_list|(
name|ShiftAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|TermAttribute
name|termAtt
init|=
operator|(
name|TermAttribute
operator|)
name|stream
operator|.
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
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
name|precisionStep
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
literal|"Shift value"
argument_list|,
name|shift
argument_list|,
name|shiftAtt
operator|.
name|getShift
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Term is correctly encoded"
argument_list|,
name|TrieUtils
operator|.
name|longToPrefixCoded
argument_list|(
name|value
argument_list|,
name|shift
argument_list|)
argument_list|,
name|termAtt
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
literal|"No more tokens available"
argument_list|,
name|stream
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testStreamOldAPI
specifier|public
name|void
name|testStreamOldAPI
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|LongTrieTokenStream
name|stream
init|=
operator|new
name|LongTrieTokenStream
argument_list|(
name|value
argument_list|,
name|precisionStep
argument_list|)
decl_stmt|;
name|stream
operator|.
name|setUseNewAPI
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Token
name|tok
init|=
operator|new
name|Token
argument_list|()
decl_stmt|;
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
name|precisionStep
control|)
block|{
name|assertNotNull
argument_list|(
literal|"New token is available"
argument_list|,
name|tok
operator|=
name|stream
operator|.
name|next
argument_list|(
name|tok
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Term is correctly encoded"
argument_list|,
name|TrieUtils
operator|.
name|longToPrefixCoded
argument_list|(
name|value
argument_list|,
name|shift
argument_list|)
argument_list|,
name|tok
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertNull
argument_list|(
literal|"No more tokens available"
argument_list|,
name|stream
operator|.
name|next
argument_list|(
name|tok
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
