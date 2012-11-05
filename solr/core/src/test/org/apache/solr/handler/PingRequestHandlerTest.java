begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import
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
name|FileReader
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
name|request
operator|.
name|SolrQueryRequest
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
name|junit
operator|.
name|Before
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
name|Ignore
import|;
end_import
begin_class
DECL|class|PingRequestHandlerTest
specifier|public
class|class
name|PingRequestHandlerTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|fileName
specifier|private
specifier|final
name|String
name|fileName
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".server-enabled"
decl_stmt|;
DECL|field|healthcheckFile
specifier|private
name|File
name|healthcheckFile
init|=
literal|null
decl_stmt|;
DECL|field|handler
specifier|private
name|PingRequestHandler
name|handler
init|=
literal|null
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|IOException
block|{
comment|// by default, use relative file in dataDir
name|healthcheckFile
operator|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
name|String
name|fileNameParam
init|=
name|fileName
decl_stmt|;
comment|// sometimes randomly use an absolute File path instead
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|healthcheckFile
operator|=
operator|new
name|File
argument_list|(
name|TEMP_DIR
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
name|fileNameParam
operator|=
name|healthcheckFile
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|healthcheckFile
operator|.
name|exists
argument_list|()
condition|)
name|FileUtils
operator|.
name|forceDelete
argument_list|(
name|healthcheckFile
argument_list|)
expr_stmt|;
name|handler
operator|=
operator|new
name|PingRequestHandler
argument_list|()
expr_stmt|;
name|NamedList
name|initParams
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|initParams
operator|.
name|add
argument_list|(
name|PingRequestHandler
operator|.
name|HEALTHCHECK_FILE_PARAM
argument_list|,
name|fileNameParam
argument_list|)
expr_stmt|;
name|handler
operator|.
name|init
argument_list|(
name|initParams
argument_list|)
expr_stmt|;
name|handler
operator|.
name|inform
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPingWithNoHealthCheck
specifier|public
name|void
name|testPingWithNoHealthCheck
parameter_list|()
throws|throws
name|Exception
block|{
comment|// for this test, we don't want any healthcheck file configured at all
name|handler
operator|=
operator|new
name|PingRequestHandler
argument_list|()
expr_stmt|;
name|handler
operator|.
name|init
argument_list|(
operator|new
name|NamedList
argument_list|()
argument_list|)
expr_stmt|;
name|handler
operator|.
name|inform
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|)
expr_stmt|;
name|SolrQueryResponse
name|rsp
init|=
literal|null
decl_stmt|;
name|rsp
operator|=
name|makeRequest
argument_list|(
name|handler
argument_list|,
name|req
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"OK"
argument_list|,
name|rsp
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
argument_list|)
expr_stmt|;
name|rsp
operator|=
name|makeRequest
argument_list|(
name|handler
argument_list|,
name|req
argument_list|(
literal|"action"
argument_list|,
literal|"ping"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"OK"
argument_list|,
name|rsp
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testEnablingServer
specifier|public
name|void
name|testEnablingServer
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
operator|!
name|healthcheckFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// first make sure that ping responds back that the service is disabled
name|SolrQueryResponse
name|sqr
init|=
name|makeRequest
argument_list|(
name|handler
argument_list|,
name|req
argument_list|()
argument_list|)
decl_stmt|;
name|SolrException
name|se
init|=
operator|(
name|SolrException
operator|)
name|sqr
operator|.
name|getException
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Response should have been replaced with a 503 SolrException."
argument_list|,
name|se
operator|.
name|code
argument_list|()
argument_list|,
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVICE_UNAVAILABLE
operator|.
name|code
argument_list|)
expr_stmt|;
comment|// now enable
name|makeRequest
argument_list|(
name|handler
argument_list|,
name|req
argument_list|(
literal|"action"
argument_list|,
literal|"enable"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|healthcheckFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|FileUtils
operator|.
name|readFileToString
argument_list|(
name|healthcheckFile
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
comment|// now verify that the handler response with success
name|SolrQueryResponse
name|rsp
init|=
name|makeRequest
argument_list|(
name|handler
argument_list|,
name|req
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"OK"
argument_list|,
name|rsp
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
argument_list|)
expr_stmt|;
comment|// enable when already enabled shouldn't cause any problems
name|makeRequest
argument_list|(
name|handler
argument_list|,
name|req
argument_list|(
literal|"action"
argument_list|,
literal|"enable"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|healthcheckFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDisablingServer
specifier|public
name|void
name|testDisablingServer
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
operator|!
name|healthcheckFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|healthcheckFile
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
comment|// first make sure that ping responds back that the service is enabled
name|SolrQueryResponse
name|rsp
init|=
name|makeRequest
argument_list|(
name|handler
argument_list|,
name|req
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"OK"
argument_list|,
name|rsp
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
argument_list|)
expr_stmt|;
comment|// now disable
name|makeRequest
argument_list|(
name|handler
argument_list|,
name|req
argument_list|(
literal|"action"
argument_list|,
literal|"disable"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|healthcheckFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// now make sure that ping responds back that the service is disabled
name|SolrQueryResponse
name|sqr
init|=
name|makeRequest
argument_list|(
name|handler
argument_list|,
name|req
argument_list|()
argument_list|)
decl_stmt|;
name|SolrException
name|se
init|=
operator|(
name|SolrException
operator|)
name|sqr
operator|.
name|getException
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Response should have been replaced with a 503 SolrException."
argument_list|,
name|se
operator|.
name|code
argument_list|()
argument_list|,
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVICE_UNAVAILABLE
operator|.
name|code
argument_list|)
expr_stmt|;
comment|// disable when already disabled shouldn't cause any problems
name|makeRequest
argument_list|(
name|handler
argument_list|,
name|req
argument_list|(
literal|"action"
argument_list|,
literal|"disable"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|healthcheckFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testGettingStatus
specifier|public
name|void
name|testGettingStatus
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrQueryResponse
name|rsp
init|=
literal|null
decl_stmt|;
name|handler
operator|.
name|handleEnable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|rsp
operator|=
name|makeRequest
argument_list|(
name|handler
argument_list|,
name|req
argument_list|(
literal|"action"
argument_list|,
literal|"status"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"enabled"
argument_list|,
name|rsp
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleEnable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|rsp
operator|=
name|makeRequest
argument_list|(
name|handler
argument_list|,
name|req
argument_list|(
literal|"action"
argument_list|,
literal|"status"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"disabled"
argument_list|,
name|rsp
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testBadActionRaisesException
specifier|public
name|void
name|testBadActionRaisesException
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|SolrQueryResponse
name|rsp
init|=
name|makeRequest
argument_list|(
name|handler
argument_list|,
name|req
argument_list|(
literal|"action"
argument_list|,
literal|"badaction"
argument_list|)
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Should have thrown a SolrException for the bad action"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|se
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
operator|.
name|code
argument_list|,
name|se
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Helper Method: Executes the request against the handler, returns     * the response, and closes the request.    */
DECL|method|makeRequest
specifier|private
name|SolrQueryResponse
name|makeRequest
parameter_list|(
name|PingRequestHandler
name|handler
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
try|try
block|{
name|handler
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|rsp
return|;
block|}
block|}
end_class
end_unit
