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
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|TestPerFieldAnalzyerWrapper
specifier|public
class|class
name|TestPerFieldAnalzyerWrapper
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testPerField
specifier|public
name|void
name|testPerField
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|text
init|=
literal|"Qwerty"
decl_stmt|;
name|PerFieldAnalyzerWrapper
name|analyzer
init|=
operator|new
name|PerFieldAnalyzerWrapper
argument_list|(
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|analyzer
operator|.
name|addAnalyzer
argument_list|(
literal|"special"
argument_list|,
operator|new
name|SimpleAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
name|TokenStream
name|tokenStream
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|"field"
argument_list|,
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
decl_stmt|;
name|TermAttribute
name|termAtt
init|=
name|tokenStream
operator|.
name|getAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tokenStream
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"WhitespaceAnalyzer does not lowercase"
argument_list|,
literal|"Qwerty"
argument_list|,
name|termAtt
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|tokenStream
operator|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|"special"
argument_list|,
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
expr_stmt|;
name|termAtt
operator|=
name|tokenStream
operator|.
name|getAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tokenStream
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"SimpleAnalyzer lowercases"
argument_list|,
literal|"qwerty"
argument_list|,
name|termAtt
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
