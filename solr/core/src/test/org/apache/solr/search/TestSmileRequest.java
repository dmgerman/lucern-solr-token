begin_unit
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|solr
operator|.
name|JSONTestUtil
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
name|SolrTestCaseHS
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
name|SolrClient
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
name|BinaryResponseParser
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
name|HttpSolrClient
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
name|request
operator|.
name|QueryRequest
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
name|cloud
operator|.
name|ZkStateReader
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
name|SmileWriterTest
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
name|search
operator|.
name|json
operator|.
name|TestJsonRequest
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
annotation|@
name|SolrTestCaseJ4
operator|.
name|SuppressSSL
DECL|class|TestSmileRequest
specifier|public
class|class
name|TestSmileRequest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|servers
specifier|private
specifier|static
name|SolrTestCaseHS
operator|.
name|SolrInstances
name|servers
decl_stmt|;
comment|// for distributed testing
annotation|@
name|BeforeClass
DECL|method|beforeTests
specifier|public
specifier|static
name|void
name|beforeTests
parameter_list|()
throws|throws
name|Exception
block|{
name|JSONTestUtil
operator|.
name|failRepeatedKeys
operator|=
literal|true
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig-tlog.xml"
argument_list|,
literal|"schema_latest.xml"
argument_list|)
expr_stmt|;
block|}
DECL|method|initServers
specifier|public
specifier|static
name|void
name|initServers
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|servers
operator|==
literal|null
condition|)
block|{
name|servers
operator|=
operator|new
name|SolrTestCaseHS
operator|.
name|SolrInstances
argument_list|(
literal|3
argument_list|,
literal|"solrconfig-tlog.xml"
argument_list|,
literal|"schema_latest.xml"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|AfterClass
DECL|method|afterTests
specifier|public
specifier|static
name|void
name|afterTests
parameter_list|()
throws|throws
name|Exception
block|{
name|JSONTestUtil
operator|.
name|failRepeatedKeys
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|servers
operator|!=
literal|null
condition|)
block|{
name|servers
operator|.
name|stop
argument_list|()
expr_stmt|;
name|servers
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDistribJsonRequest
specifier|public
name|void
name|testDistribJsonRequest
parameter_list|()
throws|throws
name|Exception
block|{
name|initServers
argument_list|()
expr_stmt|;
name|SolrTestCaseHS
operator|.
name|Client
name|client
init|=
name|servers
operator|.
name|getClient
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|)
decl_stmt|;
name|client
operator|.
name|tester
operator|=
operator|new
name|SolrTestCaseHS
operator|.
name|Client
operator|.
name|Tester
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|assertJQ
parameter_list|(
name|SolrClient
name|client
parameter_list|,
name|SolrParams
name|args
parameter_list|,
name|String
modifier|...
name|tests
parameter_list|)
throws|throws
name|Exception
block|{
operator|(
operator|(
name|HttpSolrClient
operator|)
name|client
operator|)
operator|.
name|setParser
argument_list|(
name|SmileResponseParser
operator|.
name|inst
argument_list|)
expr_stmt|;
name|QueryRequest
name|query
init|=
operator|new
name|QueryRequest
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|args
operator|.
name|get
argument_list|(
literal|"qt"
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|query
operator|.
name|setPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
name|NamedList
argument_list|<
name|Object
argument_list|>
name|rsp
init|=
name|client
operator|.
name|request
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|Map
name|m
init|=
name|rsp
operator|.
name|asMap
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|jsonStr
init|=
name|ZkStateReader
operator|.
name|toJSONString
argument_list|(
name|m
argument_list|)
decl_stmt|;
name|SolrTestCaseHS
operator|.
name|matchJSON
argument_list|(
name|jsonStr
argument_list|,
name|tests
argument_list|)
expr_stmt|;
block|}
block|}
expr_stmt|;
name|client
operator|.
name|queryDefaults
argument_list|()
operator|.
name|set
argument_list|(
literal|"shards"
argument_list|,
name|servers
operator|.
name|getShards
argument_list|()
argument_list|)
expr_stmt|;
name|TestJsonRequest
operator|.
name|doJsonRequest
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
comment|//adding this to core adds the dependency on a few extra jars to our distribution.
comment|// So this is not added there
DECL|class|SmileResponseParser
specifier|public
specifier|static
class|class
name|SmileResponseParser
extends|extends
name|BinaryResponseParser
block|{
DECL|field|inst
specifier|public
specifier|static
specifier|final
name|SmileResponseParser
name|inst
init|=
operator|new
name|SmileResponseParser
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|getWriterType
specifier|public
name|String
name|getWriterType
parameter_list|()
block|{
return|return
literal|"smile"
return|;
block|}
annotation|@
name|Override
DECL|method|processResponse
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|processResponse
parameter_list|(
name|InputStream
name|body
parameter_list|,
name|String
name|encoding
parameter_list|)
block|{
try|try
block|{
name|Map
name|m
init|=
operator|(
name|Map
operator|)
name|SmileWriterTest
operator|.
name|decodeSmile
argument_list|(
name|body
argument_list|)
decl_stmt|;
return|return
operator|new
name|NamedList
argument_list|(
name|m
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class
end_unit
