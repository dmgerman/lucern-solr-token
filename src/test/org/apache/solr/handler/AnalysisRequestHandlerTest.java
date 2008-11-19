begin_unit
begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
package|;
end_package
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|util
operator|.
name|NamedList
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
name|util
operator|.
name|AbstractSolrTestCase
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLInputFactory
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamReader
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
begin_class
DECL|class|AnalysisRequestHandlerTest
specifier|public
class|class
name|AnalysisRequestHandlerTest
extends|extends
name|AbstractSolrTestCase
block|{
DECL|field|inputFactory
specifier|private
name|XMLInputFactory
name|inputFactory
init|=
name|XMLInputFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema.xml"
return|;
block|}
annotation|@
name|Override
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig.xml"
return|;
block|}
DECL|method|testReadDoc
specifier|public
name|void
name|testReadDoc
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|xml
init|=
literal|"<docs><doc>"
operator|+
literal|"<field name=\"id\">12345</field>"
operator|+
literal|"<field name=\"name\">cute little kitten</field>"
operator|+
literal|"<field name=\"text\">the quick red fox jumped over the lazy brown dogs</field>"
operator|+
literal|"</doc>"
operator|+
literal|"<doc>"
operator|+
literal|"<field name=\"id\">12346</field>"
operator|+
literal|"<field name=\"name\">big mean dog</field>"
operator|+
literal|"<field name=\"text\">cats like to purr</field>"
operator|+
literal|"</doc>"
operator|+
literal|"</docs>"
decl_stmt|;
name|XMLStreamReader
name|parser
init|=
name|inputFactory
operator|.
name|createXMLStreamReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xml
argument_list|)
argument_list|)
decl_stmt|;
name|AnalysisRequestHandler
name|handler
init|=
operator|new
name|AnalysisRequestHandler
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|result
init|=
name|handler
operator|.
name|processContent
argument_list|(
name|parser
argument_list|,
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSchema
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"result is null and it shouldn't be"
argument_list|,
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
name|theTokens
init|=
operator|(
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
operator|)
name|result
operator|.
name|get
argument_list|(
literal|"12345"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"theTokens is null and it shouldn't be"
argument_list|,
name|theTokens
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|tokens
init|=
name|theTokens
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"tokens is null and it shouldn't be"
argument_list|,
name|tokens
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"tokens Size: "
operator|+
name|tokens
operator|.
name|size
argument_list|()
operator|+
literal|" is not : "
operator|+
literal|3
argument_list|,
name|tokens
operator|.
name|size
argument_list|()
operator|==
literal|3
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|token
decl_stmt|;
name|String
name|value
decl_stmt|;
name|token
operator|=
name|tokens
operator|.
name|get
argument_list|(
literal|"token"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|value
operator|=
operator|(
name|String
operator|)
name|token
operator|.
name|get
argument_list|(
literal|"value"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|value
operator|+
literal|" is not equal to "
operator|+
literal|"cute"
argument_list|,
name|value
operator|.
name|equals
argument_list|(
literal|"cute"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|token
operator|=
name|tokens
operator|.
name|get
argument_list|(
literal|"token"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|value
operator|=
operator|(
name|String
operator|)
name|token
operator|.
name|get
argument_list|(
literal|"value"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|value
operator|+
literal|" is not equal to "
operator|+
literal|"little"
argument_list|,
name|value
operator|.
name|equals
argument_list|(
literal|"little"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|token
operator|=
name|tokens
operator|.
name|get
argument_list|(
literal|"token"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|value
operator|=
operator|(
name|String
operator|)
name|token
operator|.
name|get
argument_list|(
literal|"value"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|value
operator|+
literal|" is not equal to "
operator|+
literal|"kitten"
argument_list|,
name|value
operator|.
name|equals
argument_list|(
literal|"kitten"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|tokens
operator|=
name|theTokens
operator|.
name|get
argument_list|(
literal|"text"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"tokens is null and it shouldn't be"
argument_list|,
name|tokens
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"tokens Size: "
operator|+
name|tokens
operator|.
name|size
argument_list|()
operator|+
literal|" is not : "
operator|+
literal|8
argument_list|,
name|tokens
operator|.
name|size
argument_list|()
operator|==
literal|8
argument_list|)
expr_stmt|;
comment|//stopwords are removed
name|String
index|[]
name|gold
init|=
operator|new
name|String
index|[]
block|{
literal|"quick"
block|,
literal|"red"
block|,
literal|"fox"
block|,
literal|"jump"
block|,
literal|"over"
block|,
literal|"lazi"
block|,
literal|"brown"
block|,
literal|"dog"
block|}
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|gold
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|tok
init|=
name|tokens
operator|.
name|get
argument_list|(
literal|"token"
argument_list|,
name|j
argument_list|)
decl_stmt|;
name|value
operator|=
operator|(
name|String
operator|)
name|tok
operator|.
name|get
argument_list|(
literal|"value"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|value
operator|+
literal|" is not equal to "
operator|+
name|gold
index|[
name|j
index|]
argument_list|,
name|value
operator|.
name|equals
argument_list|(
name|gold
index|[
name|j
index|]
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
block|}
name|theTokens
operator|=
operator|(
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
operator|)
name|result
operator|.
name|get
argument_list|(
literal|"12346"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"theTokens is null and it shouldn't be"
argument_list|,
name|theTokens
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|tokens
operator|=
name|theTokens
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"tokens is null and it shouldn't be"
argument_list|,
name|tokens
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"tokens Size: "
operator|+
name|tokens
operator|.
name|size
argument_list|()
operator|+
literal|" is not : "
operator|+
literal|3
argument_list|,
name|tokens
operator|.
name|size
argument_list|()
operator|==
literal|3
argument_list|)
expr_stmt|;
name|gold
operator|=
operator|new
name|String
index|[]
block|{
literal|"cat"
block|,
literal|"like"
block|,
literal|"purr"
block|}
expr_stmt|;
name|tokens
operator|=
name|theTokens
operator|.
name|get
argument_list|(
literal|"text"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"tokens is null and it shouldn't be"
argument_list|,
name|tokens
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"tokens Size: "
operator|+
name|tokens
operator|.
name|size
argument_list|()
operator|+
literal|" is not : "
operator|+
literal|3
argument_list|,
name|tokens
operator|.
name|size
argument_list|()
operator|==
literal|3
argument_list|)
expr_stmt|;
comment|//stopwords are removed
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|gold
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|tok
init|=
name|tokens
operator|.
name|get
argument_list|(
literal|"token"
argument_list|,
name|j
argument_list|)
decl_stmt|;
name|value
operator|=
operator|(
name|String
operator|)
name|tok
operator|.
name|get
argument_list|(
literal|"value"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|value
operator|+
literal|" is not equal to "
operator|+
name|gold
index|[
name|j
index|]
argument_list|,
name|value
operator|.
name|equals
argument_list|(
name|gold
index|[
name|j
index|]
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
