begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.miscellaneous
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|miscellaneous
package|;
end_package
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
begin_class
DECL|class|TestLimitTokenCountFilterFactory
specifier|public
class|class
name|TestLimitTokenCountFilterFactory
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|IOException
block|{
name|LimitTokenCountFilterFactory
name|factory
init|=
operator|new
name|LimitTokenCountFilterFactory
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|LimitTokenCountFilterFactory
operator|.
name|MAX_TOKEN_COUNT_KEY
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|String
name|test
init|=
literal|"A1 B2 C3 D4 E5 F6"
decl_stmt|;
name|MockTokenizer
name|tok
init|=
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|test
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// LimitTokenCountFilter doesn't consume the entire stream that it wraps
name|tok
operator|.
name|setEnableChecks
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
name|tok
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
literal|"A1"
block|,
literal|"B2"
block|,
literal|"C3"
block|}
argument_list|)
expr_stmt|;
comment|// param is required
name|factory
operator|=
operator|new
name|LimitTokenCountFilterFactory
argument_list|()
expr_stmt|;
name|args
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|IllegalArgumentException
name|iae
init|=
literal|null
decl_stmt|;
try|try
block|{
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"exception doesn't mention param: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|0
operator|<
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|indexOf
argument_list|(
name|LimitTokenCountFilterFactory
operator|.
name|MAX_TOKEN_COUNT_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|iae
operator|=
name|e
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
literal|"no exception thrown"
argument_list|,
name|iae
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
