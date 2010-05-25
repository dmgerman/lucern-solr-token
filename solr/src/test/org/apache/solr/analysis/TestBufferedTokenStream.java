begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
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
name|Tokenizer
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
begin_comment
comment|/**  * Test that BufferedTokenStream behaves as advertised in subclasses.  */
end_comment
begin_class
DECL|class|TestBufferedTokenStream
specifier|public
class|class
name|TestBufferedTokenStream
extends|extends
name|BaseTokenTestCase
block|{
comment|/** Example of a class implementing the rule "A" "B" => "Q" "B" */
DECL|class|AB_Q_Stream
specifier|public
specifier|static
class|class
name|AB_Q_Stream
extends|extends
name|BufferedTokenStream
block|{
DECL|method|AB_Q_Stream
specifier|public
name|AB_Q_Stream
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
DECL|method|process
specifier|protected
name|Token
name|process
parameter_list|(
name|Token
name|t
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
literal|"A"
operator|.
name|equals
argument_list|(
operator|new
name|String
argument_list|(
name|t
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|t
operator|.
name|termLength
argument_list|()
argument_list|)
argument_list|)
condition|)
block|{
name|Token
name|t2
init|=
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|t2
operator|!=
literal|null
operator|&&
literal|"B"
operator|.
name|equals
argument_list|(
operator|new
name|String
argument_list|(
name|t2
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|t2
operator|.
name|termLength
argument_list|()
argument_list|)
argument_list|)
condition|)
name|t
operator|.
name|setTermBuffer
argument_list|(
literal|"Q"
argument_list|)
expr_stmt|;
if|if
condition|(
name|t2
operator|!=
literal|null
condition|)
name|pushBack
argument_list|(
name|t2
argument_list|)
expr_stmt|;
block|}
return|return
name|t
return|;
block|}
block|}
comment|/** Example of a class implementing "A" "B" => "A" "A" "B" */
DECL|class|AB_AAB_Stream
specifier|public
specifier|static
class|class
name|AB_AAB_Stream
extends|extends
name|BufferedTokenStream
block|{
DECL|method|AB_AAB_Stream
specifier|public
name|AB_AAB_Stream
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
DECL|method|process
specifier|protected
name|Token
name|process
parameter_list|(
name|Token
name|t
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
literal|"A"
operator|.
name|equals
argument_list|(
operator|new
name|String
argument_list|(
name|t
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|t
operator|.
name|termLength
argument_list|()
argument_list|)
argument_list|)
operator|&&
literal|"B"
operator|.
name|equals
argument_list|(
operator|new
name|String
argument_list|(
name|peek
argument_list|(
literal|1
argument_list|)
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|peek
argument_list|(
literal|1
argument_list|)
operator|.
name|termLength
argument_list|()
argument_list|)
argument_list|)
condition|)
name|write
argument_list|(
operator|(
name|Token
operator|)
name|t
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
block|}
DECL|method|testABQ
specifier|public
name|void
name|testABQ
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|input
init|=
literal|"How now A B brown A cow B like A B thing?"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"How now Q B brown A cow B like Q B thing?"
decl_stmt|;
name|TokenStream
name|ts
init|=
operator|new
name|AB_Q_Stream
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
name|expected
operator|.
name|split
argument_list|(
literal|"\\s"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testABAAB
specifier|public
name|void
name|testABAAB
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|input
init|=
literal|"How now A B brown A cow B like A B thing?"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"How now A A B brown A cow B like A A B thing?"
decl_stmt|;
name|TokenStream
name|ts
init|=
operator|new
name|AB_AAB_Stream
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
name|expected
operator|.
name|split
argument_list|(
literal|"\\s"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testReset
specifier|public
name|void
name|testReset
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|input
init|=
literal|"How now A B brown A cow B like A B thing?"
decl_stmt|;
name|Tokenizer
name|tokenizer
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
operator|new
name|AB_AAB_Stream
argument_list|(
name|tokenizer
argument_list|)
decl_stmt|;
name|CharTermAttribute
name|term
init|=
name|ts
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|ts
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"How"
argument_list|,
name|term
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ts
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"now"
argument_list|,
name|term
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ts
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"A"
argument_list|,
name|term
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// reset back to input,
comment|// if reset() does not work correctly then previous buffered tokens will remain
name|tokenizer
operator|.
name|reset
argument_list|(
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
name|ts
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|ts
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"How"
argument_list|,
name|term
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
