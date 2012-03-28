begin_unit
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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|IndexSchema
import|;
end_import
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Test for {@link MorfologikFilterFactory}.  */
end_comment
begin_class
DECL|class|TestMorfologikFilterFactory
specifier|public
class|class
name|TestMorfologikFilterFactory
extends|extends
name|BaseTokenTestCase
block|{
DECL|method|testCreateDictionary
specifier|public
name|void
name|testCreateDictionary
parameter_list|()
throws|throws
name|Exception
block|{
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"rowery bilety"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|initParams
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
name|initParams
operator|.
name|put
argument_list|(
name|IndexSchema
operator|.
name|LUCENE_MATCH_VERSION_PARAM
argument_list|,
name|DEFAULT_VERSION
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|initParams
operator|.
name|put
argument_list|(
name|MorfologikFilterFactory
operator|.
name|DICTIONARY_SCHEMA_ATTRIBUTE
argument_list|,
literal|"morfologik"
argument_list|)
expr_stmt|;
name|MorfologikFilterFactory
name|factory
init|=
operator|new
name|MorfologikFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|initParams
argument_list|)
expr_stmt|;
name|TokenStream
name|ts
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
name|reader
argument_list|)
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
literal|"rower"
block|,
literal|"bilet"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
