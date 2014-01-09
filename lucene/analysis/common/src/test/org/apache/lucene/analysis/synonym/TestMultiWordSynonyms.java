begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.synonym
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|synonym
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
name|util
operator|.
name|BaseTokenStreamFactoryTestCase
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
name|StringMockResourceLoader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
comment|/**  * @since solr 1.4  */
end_comment
begin_class
DECL|class|TestMultiWordSynonyms
specifier|public
class|class
name|TestMultiWordSynonyms
extends|extends
name|BaseTokenStreamFactoryTestCase
block|{
DECL|method|testMultiWordSynonyms
specifier|public
name|void
name|testMultiWordSynonyms
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"a e"
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
name|whitespaceMockTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|stream
operator|=
name|tokenFilterFactory
argument_list|(
literal|"Synonym"
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringMockResourceLoader
argument_list|(
literal|"a b c,d"
argument_list|)
argument_list|,
literal|"synonyms"
argument_list|,
literal|"synonyms.txt"
argument_list|)
operator|.
name|create
argument_list|(
name|stream
argument_list|)
expr_stmt|;
comment|// This fails because ["e","e"] is the value of the token stream
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"e"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
