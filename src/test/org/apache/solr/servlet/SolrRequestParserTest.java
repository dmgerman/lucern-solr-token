begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.servlet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|servlet
package|;
end_package
begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|createMock
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|expect
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|replay
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|List
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
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|params
operator|.
name|CommonParams
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
name|params
operator|.
name|MultiMapSolrParams
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
name|params
operator|.
name|SolrParams
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
name|util
operator|.
name|ContentStream
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
name|core
operator|.
name|SolrCore
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
begin_class
DECL|class|SolrRequestParserTest
specifier|public
class|class
name|SolrRequestParserTest
extends|extends
name|AbstractSolrTestCase
block|{
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
DECL|field|parser
name|SolrRequestParsers
name|parser
decl_stmt|;
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
name|parser
operator|=
operator|new
name|SolrRequestParsers
argument_list|(
name|SolrCore
operator|.
name|getSolrCore
argument_list|()
argument_list|,
name|SolrConfig
operator|.
name|config
argument_list|)
expr_stmt|;
block|}
DECL|method|testStreamBody
specifier|public
name|void
name|testStreamBody
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|body1
init|=
literal|"AMANAPLANPANAMA"
decl_stmt|;
name|String
name|body2
init|=
literal|"qwertasdfgzxcvb"
decl_stmt|;
name|String
name|body3
init|=
literal|"1234567890"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|STREAM_BODY
argument_list|,
operator|new
name|String
index|[]
block|{
name|body1
block|}
argument_list|)
expr_stmt|;
comment|// Make sure it got a single stream in and out ok
name|List
argument_list|<
name|ContentStream
argument_list|>
name|streams
init|=
operator|new
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
argument_list|()
decl_stmt|;
name|parser
operator|.
name|buildRequestFrom
argument_list|(
operator|new
name|MultiMapSolrParams
argument_list|(
name|args
argument_list|)
argument_list|,
name|streams
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|streams
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|body1
argument_list|,
name|IOUtils
operator|.
name|toString
argument_list|(
name|streams
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStream
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now add three and make sure they come out ok
name|streams
operator|=
operator|new
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|STREAM_BODY
argument_list|,
operator|new
name|String
index|[]
block|{
name|body1
block|,
name|body2
block|,
name|body3
block|}
argument_list|)
expr_stmt|;
name|parser
operator|.
name|buildRequestFrom
argument_list|(
operator|new
name|MultiMapSolrParams
argument_list|(
name|args
argument_list|)
argument_list|,
name|streams
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|streams
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|input
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|output
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|input
operator|.
name|add
argument_list|(
name|body1
argument_list|)
expr_stmt|;
name|input
operator|.
name|add
argument_list|(
name|body2
argument_list|)
expr_stmt|;
name|input
operator|.
name|add
argument_list|(
name|body3
argument_list|)
expr_stmt|;
name|output
operator|.
name|add
argument_list|(
name|IOUtils
operator|.
name|toString
argument_list|(
name|streams
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStream
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|output
operator|.
name|add
argument_list|(
name|IOUtils
operator|.
name|toString
argument_list|(
name|streams
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getStream
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|output
operator|.
name|add
argument_list|(
name|IOUtils
operator|.
name|toString
argument_list|(
name|streams
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getStream
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// sort them so the output is consistent
name|Collections
operator|.
name|sort
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|input
operator|.
name|toString
argument_list|()
argument_list|,
name|output
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// set the contentType and make sure tat gets set
name|String
name|ctype
init|=
literal|"text/xxx"
decl_stmt|;
name|streams
operator|=
operator|new
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|STREAM_CONTENTTYPE
argument_list|,
operator|new
name|String
index|[]
block|{
name|ctype
block|}
argument_list|)
expr_stmt|;
name|parser
operator|.
name|buildRequestFrom
argument_list|(
operator|new
name|MultiMapSolrParams
argument_list|(
name|args
argument_list|)
argument_list|,
name|streams
argument_list|)
expr_stmt|;
for|for
control|(
name|ContentStream
name|s
range|:
name|streams
control|)
block|{
name|assertEquals
argument_list|(
name|ctype
argument_list|,
name|s
operator|.
name|getContentType
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testStreamURL
specifier|public
name|void
name|testStreamURL
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|ok
init|=
literal|false
decl_stmt|;
name|String
name|url
init|=
literal|"http://svn.apache.org/repos/asf/lucene/solr/trunk/"
decl_stmt|;
name|String
name|txt
init|=
literal|null
decl_stmt|;
try|try
block|{
name|txt
operator|=
name|IOUtils
operator|.
name|toString
argument_list|(
operator|new
name|URL
argument_list|(
name|url
argument_list|)
operator|.
name|openStream
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
comment|// TODO - should it fail/skip?
name|fail
argument_list|(
literal|"this test only works if you have a network connection."
argument_list|)
expr_stmt|;
return|return;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|STREAM_URL
argument_list|,
operator|new
name|String
index|[]
block|{
name|url
block|}
argument_list|)
expr_stmt|;
comment|// Make sure it got a single stream in and out ok
name|List
argument_list|<
name|ContentStream
argument_list|>
name|streams
init|=
operator|new
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
argument_list|()
decl_stmt|;
name|parser
operator|.
name|buildRequestFrom
argument_list|(
operator|new
name|MultiMapSolrParams
argument_list|(
name|args
argument_list|)
argument_list|,
name|streams
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|streams
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|txt
argument_list|,
name|IOUtils
operator|.
name|toString
argument_list|(
name|streams
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStream
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testUrlParamParsing
specifier|public
name|void
name|testUrlParamParsing
parameter_list|()
block|{
name|String
index|[]
index|[]
name|teststr
init|=
operator|new
name|String
index|[]
index|[]
block|{
block|{
literal|"this is simple"
block|,
literal|"this%20is%20simple"
block|}
block|,
block|{
literal|"this is simple"
block|,
literal|"this+is+simple"
block|}
block|,
block|{
literal|"\u00FC"
block|,
literal|"%C3%BC"
block|}
block|,
comment|// lower-case "u" with diaeresis/umlaut
block|{
literal|"\u0026"
block|,
literal|"%26"
block|}
block|,
comment|//&
block|{
literal|"\u20AC"
block|,
literal|"%E2%82%AC"
block|}
comment|// euro
block|}
decl_stmt|;
for|for
control|(
name|String
index|[]
name|tst
range|:
name|teststr
control|)
block|{
name|MultiMapSolrParams
name|params
init|=
name|SolrRequestParsers
operator|.
name|parseQueryString
argument_list|(
literal|"val="
operator|+
name|tst
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|tst
index|[
literal|0
index|]
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"val"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testStandardParseParamsAndFillStreams
specifier|public
name|void
name|testStandardParseParamsAndFillStreams
parameter_list|()
throws|throws
name|Exception
block|{
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
name|streams
init|=
operator|new
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"q"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"hello"
block|}
argument_list|)
expr_stmt|;
comment|// Set up the expected behavior
name|String
index|[]
name|ct
init|=
operator|new
name|String
index|[]
block|{
literal|"application/x-www-form-urlencoded"
block|,
literal|"Application/x-www-form-urlencoded"
block|,
literal|"application/x-www-form-urlencoded; charset=utf-8"
block|,
literal|"application/x-www-form-urlencoded;"
block|}
decl_stmt|;
for|for
control|(
name|String
name|contentType
range|:
name|ct
control|)
block|{
name|HttpServletRequest
name|request
init|=
name|createMock
argument_list|(
name|HttpServletRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|expect
argument_list|(
name|request
operator|.
name|getMethod
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|"POST"
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|expect
argument_list|(
name|request
operator|.
name|getContentType
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|contentType
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|expect
argument_list|(
name|request
operator|.
name|getParameterMap
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|params
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|replay
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|MultipartRequestParser
name|multipart
init|=
operator|new
name|MultipartRequestParser
argument_list|(
literal|1000000
argument_list|)
decl_stmt|;
name|RawRequestParser
name|raw
init|=
operator|new
name|RawRequestParser
argument_list|()
decl_stmt|;
name|StandardRequestParser
name|standard
init|=
operator|new
name|StandardRequestParser
argument_list|(
name|multipart
argument_list|,
name|raw
argument_list|)
decl_stmt|;
name|SolrParams
name|p
init|=
name|standard
operator|.
name|parseParamsAndFillStreams
argument_list|(
name|request
argument_list|,
name|streams
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"contentType: "
operator|+
name|contentType
argument_list|,
literal|"hello"
argument_list|,
name|p
operator|.
name|get
argument_list|(
literal|"q"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
