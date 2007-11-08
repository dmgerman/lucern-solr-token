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
name|el
operator|.
name|*
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
name|TokenFilter
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
name|TokenStream
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
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|core
operator|.
name|SolrConfig
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
name|common
operator|.
name|SolrException
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
name|common
operator|.
name|SolrException
operator|.
name|ErrorCode
import|;
end_import
begin_class
DECL|class|GreekLowerCaseFilterFactory
specifier|public
class|class
name|GreekLowerCaseFilterFactory
extends|extends
name|BaseTokenFilterFactory
block|{
DECL|field|CHARSETS
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|char
index|[]
argument_list|>
name|CHARSETS
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|char
index|[]
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
name|CHARSETS
operator|.
name|put
argument_list|(
literal|"UnicodeGreek"
argument_list|,
name|GreekCharsets
operator|.
name|UnicodeGreek
argument_list|)
expr_stmt|;
name|CHARSETS
operator|.
name|put
argument_list|(
literal|"ISO"
argument_list|,
name|GreekCharsets
operator|.
name|ISO
argument_list|)
expr_stmt|;
name|CHARSETS
operator|.
name|put
argument_list|(
literal|"CP1253"
argument_list|,
name|GreekCharsets
operator|.
name|CP1253
argument_list|)
expr_stmt|;
block|}
DECL|field|charset
specifier|private
name|char
index|[]
name|charset
init|=
name|GreekCharsets
operator|.
name|UnicodeGreek
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|SolrConfig
name|solrConfig
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|solrConfig
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|String
name|charsetName
init|=
name|args
operator|.
name|get
argument_list|(
literal|"charset"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|charsetName
condition|)
name|charset
operator|=
name|CHARSETS
operator|.
name|get
argument_list|(
name|charsetName
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|charset
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Don't understand charset: "
operator|+
name|charsetName
argument_list|)
throw|;
block|}
block|}
DECL|method|create
specifier|public
name|GreekLowerCaseFilter
name|create
parameter_list|(
name|TokenStream
name|in
parameter_list|)
block|{
return|return
operator|new
name|GreekLowerCaseFilter
argument_list|(
name|in
argument_list|,
name|charset
argument_list|)
return|;
block|}
block|}
end_class
end_unit
