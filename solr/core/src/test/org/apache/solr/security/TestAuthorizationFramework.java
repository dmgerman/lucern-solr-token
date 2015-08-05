begin_unit
begin_package
DECL|package|org.apache.solr.security
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|security
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
name|commons
operator|.
name|io
operator|.
name|Charsets
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
name|util
operator|.
name|LuceneTestCase
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
name|cloud
operator|.
name|AbstractFullDistribZkTestBase
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
name|ModifiableSolrParams
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|CreateMode
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
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import
begin_class
annotation|@
name|LuceneTestCase
operator|.
name|Slow
DECL|class|TestAuthorizationFramework
specifier|public
class|class
name|TestAuthorizationFramework
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|log
specifier|final
specifier|private
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestAuthorizationFramework
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|TIMEOUT
specifier|static
specifier|final
name|int
name|TIMEOUT
init|=
literal|10000
decl_stmt|;
DECL|method|distribSetUp
specifier|public
name|void
name|distribSetUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|distribSetUp
argument_list|()
expr_stmt|;
try|try
init|(
name|ZkStateReader
name|zkStateReader
init|=
operator|new
name|ZkStateReader
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|TIMEOUT
argument_list|,
name|TIMEOUT
argument_list|)
init|)
block|{
name|zkStateReader
operator|.
name|getZkClient
argument_list|()
operator|.
name|create
argument_list|(
name|ZkStateReader
operator|.
name|SOLR_SECURITY_CONF_PATH
argument_list|,
literal|"{\"authorization\":{\"class\":\"org.apache.solr.security.MockAuthorizationPlugin\"}}"
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|authorizationFrameworkTest
specifier|public
name|void
name|authorizationFrameworkTest
parameter_list|()
throws|throws
name|Exception
block|{
name|MockAuthorizationPlugin
operator|.
name|denyUsers
operator|.
name|add
argument_list|(
literal|"user1"
argument_list|)
expr_stmt|;
name|MockAuthorizationPlugin
operator|.
name|denyUsers
operator|.
name|add
argument_list|(
literal|"user1"
argument_list|)
expr_stmt|;
name|waitForThingsToLevelOut
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Starting test"
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
comment|// This should work fine.
name|cloudClient
operator|.
name|query
argument_list|(
name|params
argument_list|)
expr_stmt|;
comment|// This user is blacklisted in the mock. The request should return a 403.
name|params
operator|.
name|add
argument_list|(
literal|"uname"
argument_list|,
literal|"user1"
argument_list|)
expr_stmt|;
try|try
block|{
name|cloudClient
operator|.
name|query
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"This should have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{}
name|log
operator|.
name|info
argument_list|(
literal|"Ending test"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|distribTearDown
specifier|public
name|void
name|distribTearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|distribTearDown
argument_list|()
expr_stmt|;
name|MockAuthorizationPlugin
operator|.
name|denyUsers
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
