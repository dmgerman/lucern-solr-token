begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.pattern
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|pattern
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
name|core
operator|.
name|WhitespaceTokenizer
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
name|regex
operator|.
name|Pattern
import|;
end_import
begin_comment
comment|/**  * @version $Id:$  */
end_comment
begin_class
DECL|class|TestPatternReplaceFilter
specifier|public
class|class
name|TestPatternReplaceFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testReplaceAll
specifier|public
name|void
name|testReplaceAll
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|input
init|=
literal|"aabfooaabfooabfoob ab caaaaaaaaab"
decl_stmt|;
name|TokenStream
name|ts
init|=
operator|new
name|PatternReplaceFilter
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
literal|"a*b"
argument_list|)
argument_list|,
literal|"-"
argument_list|,
literal|true
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
literal|"-foo-foo-foo-"
block|,
literal|"-"
block|,
literal|"c-"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testReplaceFirst
specifier|public
name|void
name|testReplaceFirst
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|input
init|=
literal|"aabfooaabfooabfoob ab caaaaaaaaab"
decl_stmt|;
name|TokenStream
name|ts
init|=
operator|new
name|PatternReplaceFilter
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
literal|"a*b"
argument_list|)
argument_list|,
literal|"-"
argument_list|,
literal|false
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
literal|"-fooaabfooabfoob"
block|,
literal|"-"
block|,
literal|"c-"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testStripFirst
specifier|public
name|void
name|testStripFirst
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|input
init|=
literal|"aabfooaabfooabfoob ab caaaaaaaaab"
decl_stmt|;
name|TokenStream
name|ts
init|=
operator|new
name|PatternReplaceFilter
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
literal|"a*b"
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|false
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
literal|"fooaabfooabfoob"
block|,
literal|""
block|,
literal|"c"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testStripAll
specifier|public
name|void
name|testStripAll
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|input
init|=
literal|"aabfooaabfooabfoob ab caaaaaaaaab"
decl_stmt|;
name|TokenStream
name|ts
init|=
operator|new
name|PatternReplaceFilter
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
literal|"a*b"
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|true
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
literal|"foofoofoo"
block|,
literal|""
block|,
literal|"c"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testReplaceAllWithBackRef
specifier|public
name|void
name|testReplaceAllWithBackRef
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|input
init|=
literal|"aabfooaabfooabfoob ab caaaaaaaaab"
decl_stmt|;
name|TokenStream
name|ts
init|=
operator|new
name|PatternReplaceFilter
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(a*)b"
argument_list|)
argument_list|,
literal|"$1\\$"
argument_list|,
literal|true
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
literal|"aa$fooaa$fooa$foo$"
block|,
literal|"a$"
block|,
literal|"caaaaaaaaa$"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
