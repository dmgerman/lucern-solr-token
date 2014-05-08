begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|net
operator|.
name|URI
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|FileUtils
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|Header
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpResponse
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|HttpClient
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpGet
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
name|SolrJettyTestBase
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
name|SolrTestCaseJ4
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
name|client
operator|.
name|solrj
operator|.
name|SolrServerException
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|HttpSolrServer
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
name|handler
operator|.
name|component
operator|.
name|ResponseBuilder
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
name|handler
operator|.
name|component
operator|.
name|SearchComponent
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
name|response
operator|.
name|SolrQueryResponse
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_class
DECL|class|ResponseHeaderTest
specifier|public
class|class
name|ResponseHeaderTest
extends|extends
name|SolrJettyTestBase
block|{
DECL|field|solrHomeDirectory
specifier|private
specifier|static
name|File
name|solrHomeDirectory
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeTest
specifier|public
specifier|static
name|void
name|beforeTest
parameter_list|()
throws|throws
name|Exception
block|{
name|solrHomeDirectory
operator|=
name|createTempDir
argument_list|()
expr_stmt|;
name|setupJettyTestHome
argument_list|(
name|solrHomeDirectory
argument_list|,
literal|"collection1"
argument_list|)
expr_stmt|;
name|String
name|top
init|=
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
operator|+
literal|"/collection1/conf"
decl_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|top
argument_list|,
literal|"solrconfig-headers.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|solrHomeDirectory
operator|+
literal|"/collection1/conf"
argument_list|,
literal|"solrconfig.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|createJetty
argument_list|(
name|solrHomeDirectory
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterTest
specifier|public
specifier|static
name|void
name|afterTest
parameter_list|()
throws|throws
name|Exception
block|{
name|cleanUpJettyHome
argument_list|(
name|solrHomeDirectory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHttpResponse
specifier|public
name|void
name|testHttpResponse
parameter_list|()
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|HttpSolrServer
name|client
init|=
operator|(
name|HttpSolrServer
operator|)
name|getSolrServer
argument_list|()
decl_stmt|;
name|HttpClient
name|httpClient
init|=
name|client
operator|.
name|getHttpClient
argument_list|()
decl_stmt|;
name|URI
name|uri
init|=
name|URI
operator|.
name|create
argument_list|(
name|client
operator|.
name|getBaseURL
argument_list|()
operator|+
literal|"/withHeaders?q=*:*"
argument_list|)
decl_stmt|;
name|HttpGet
name|httpGet
init|=
operator|new
name|HttpGet
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|HttpResponse
name|response
init|=
name|httpClient
operator|.
name|execute
argument_list|(
name|httpGet
argument_list|)
decl_stmt|;
name|Header
index|[]
name|headers
init|=
name|response
operator|.
name|getAllHeaders
argument_list|()
decl_stmt|;
name|boolean
name|containsWarningHeader
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Header
name|header
range|:
name|headers
control|)
block|{
if|if
condition|(
literal|"Warning"
operator|.
name|equals
argument_list|(
name|header
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|containsWarningHeader
operator|=
literal|true
expr_stmt|;
name|assertEquals
argument_list|(
literal|"This is a test warning"
argument_list|,
name|header
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
name|assertTrue
argument_list|(
literal|"Expected header not found"
argument_list|,
name|containsWarningHeader
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddHttpHeader
specifier|public
name|void
name|testAddHttpHeader
parameter_list|()
block|{
name|SolrQueryResponse
name|response
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|it
init|=
name|response
operator|.
name|httpHeaders
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|addHttpHeader
argument_list|(
literal|"key1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|it
operator|=
name|response
operator|.
name|httpHeaders
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"key1"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value1"
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|addHttpHeader
argument_list|(
literal|"key1"
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
name|it
operator|=
name|response
operator|.
name|httpHeaders
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|entry
operator|=
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"key1"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value1"
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|entry
operator|=
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"key1"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value2"
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|addHttpHeader
argument_list|(
literal|"key2"
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
name|it
operator|=
name|response
operator|.
name|httpHeaders
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|entry
operator|=
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"key1"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value1"
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|entry
operator|=
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"key1"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value2"
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|entry
operator|=
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"key2"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value2"
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSetHttpHeader
specifier|public
name|void
name|testSetHttpHeader
parameter_list|()
block|{
name|SolrQueryResponse
name|response
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|it
init|=
name|response
operator|.
name|httpHeaders
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|setHttpHeader
argument_list|(
literal|"key1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|it
operator|=
name|response
operator|.
name|httpHeaders
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"key1"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value1"
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|setHttpHeader
argument_list|(
literal|"key1"
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
name|it
operator|=
name|response
operator|.
name|httpHeaders
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|entry
operator|=
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"key1"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value2"
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|addHttpHeader
argument_list|(
literal|"key1"
argument_list|,
literal|"value3"
argument_list|)
expr_stmt|;
name|response
operator|.
name|setHttpHeader
argument_list|(
literal|"key1"
argument_list|,
literal|"value4"
argument_list|)
expr_stmt|;
name|it
operator|=
name|response
operator|.
name|httpHeaders
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|entry
operator|=
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"key1"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value4"
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|setHttpHeader
argument_list|(
literal|"key2"
argument_list|,
literal|"value5"
argument_list|)
expr_stmt|;
name|it
operator|=
name|response
operator|.
name|httpHeaders
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|entry
operator|=
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"key1"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value4"
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|entry
operator|=
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"key2"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value5"
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemoveHttpHeader
specifier|public
name|void
name|testRemoveHttpHeader
parameter_list|()
block|{
name|SolrQueryResponse
name|response
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|it
init|=
name|response
operator|.
name|httpHeaders
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|addHttpHeader
argument_list|(
literal|"key1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|response
operator|.
name|httpHeaders
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value1"
argument_list|,
name|response
operator|.
name|removeHttpHeader
argument_list|(
literal|"key1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|response
operator|.
name|httpHeaders
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|addHttpHeader
argument_list|(
literal|"key1"
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
name|response
operator|.
name|addHttpHeader
argument_list|(
literal|"key1"
argument_list|,
literal|"value3"
argument_list|)
expr_stmt|;
name|response
operator|.
name|addHttpHeader
argument_list|(
literal|"key2"
argument_list|,
literal|"value4"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|response
operator|.
name|httpHeaders
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value2"
argument_list|,
name|response
operator|.
name|removeHttpHeader
argument_list|(
literal|"key1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value3"
argument_list|,
name|response
operator|.
name|httpHeaders
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value3"
argument_list|,
name|response
operator|.
name|removeHttpHeader
argument_list|(
literal|"key1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|response
operator|.
name|removeHttpHeader
argument_list|(
literal|"key1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"key2"
argument_list|,
name|response
operator|.
name|httpHeaders
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemoveHttpHeaders
specifier|public
name|void
name|testRemoveHttpHeaders
parameter_list|()
block|{
name|SolrQueryResponse
name|response
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|it
init|=
name|response
operator|.
name|httpHeaders
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|addHttpHeader
argument_list|(
literal|"key1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|response
operator|.
name|httpHeaders
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"value1"
argument_list|)
argument_list|,
name|response
operator|.
name|removeHttpHeaders
argument_list|(
literal|"key1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|response
operator|.
name|httpHeaders
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|addHttpHeader
argument_list|(
literal|"key1"
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
name|response
operator|.
name|addHttpHeader
argument_list|(
literal|"key1"
argument_list|,
literal|"value3"
argument_list|)
expr_stmt|;
name|response
operator|.
name|addHttpHeader
argument_list|(
literal|"key2"
argument_list|,
literal|"value4"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|response
operator|.
name|httpHeaders
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"value2"
block|,
literal|"value3"
block|}
argument_list|)
argument_list|,
name|response
operator|.
name|removeHttpHeaders
argument_list|(
literal|"key1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|response
operator|.
name|removeHttpHeaders
argument_list|(
literal|"key1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"key2"
argument_list|,
name|response
operator|.
name|httpHeaders
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|ComponentThatAddsHeader
specifier|public
specifier|static
class|class
name|ComponentThatAddsHeader
extends|extends
name|SearchComponent
block|{
annotation|@
name|Override
DECL|method|prepare
specifier|public
name|void
name|prepare
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
throws|throws
name|IOException
block|{
name|rb
operator|.
name|rsp
operator|.
name|addHttpHeader
argument_list|(
literal|"Warning"
argument_list|,
literal|"This is a test warning"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|process
specifier|public
name|void
name|process
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class
end_unit
