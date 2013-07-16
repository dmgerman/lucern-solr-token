begin_unit
begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment
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
operator|.
name|Slow
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
name|cloud
operator|.
name|SolrZkClient
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
name|ZkNodeProps
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
name|core
operator|.
name|CoreContainer
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
name|CoreDescriptor
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
name|ExternalPaths
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
begin_class
annotation|@
name|Slow
DECL|class|ZkControllerTest
specifier|public
class|class
name|ZkControllerTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|COLLECTION_NAME
specifier|private
specifier|static
specifier|final
name|String
name|COLLECTION_NAME
init|=
literal|"collection1"
decl_stmt|;
DECL|field|TIMEOUT
specifier|static
specifier|final
name|int
name|TIMEOUT
init|=
literal|10000
decl_stmt|;
DECL|field|DEBUG
specifier|private
specifier|static
specifier|final
name|boolean
name|DEBUG
init|=
literal|false
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
argument_list|()
expr_stmt|;
block|}
DECL|method|testNodeNameUrlConversion
specifier|public
name|void
name|testNodeNameUrlConversion
parameter_list|()
throws|throws
name|Exception
block|{
comment|// nodeName from parts
name|assertEquals
argument_list|(
literal|"localhost:8888_solr"
argument_list|,
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"localhost"
argument_list|,
literal|"8888"
argument_list|,
literal|"solr"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"localhost:8888_solr"
argument_list|,
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"localhost"
argument_list|,
literal|"8888"
argument_list|,
literal|"/solr"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"localhost:8888_solr"
argument_list|,
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"localhost"
argument_list|,
literal|"8888"
argument_list|,
literal|"/solr/"
argument_list|)
argument_list|)
expr_stmt|;
comment|// root context
name|assertEquals
argument_list|(
literal|"localhost:8888_"
argument_list|,
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"localhost"
argument_list|,
literal|"8888"
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"localhost:8888_"
argument_list|,
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"localhost"
argument_list|,
literal|"8888"
argument_list|,
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
comment|// subdir
name|assertEquals
argument_list|(
literal|"foo-bar:77_solr%2Fsub_dir"
argument_list|,
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"foo-bar"
argument_list|,
literal|"77"
argument_list|,
literal|"solr/sub_dir"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo-bar:77_solr%2Fsub_dir"
argument_list|,
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"foo-bar"
argument_list|,
literal|"77"
argument_list|,
literal|"/solr/sub_dir"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo-bar:77_solr%2Fsub_dir"
argument_list|,
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"foo-bar"
argument_list|,
literal|"77"
argument_list|,
literal|"/solr/sub_dir/"
argument_list|)
argument_list|)
expr_stmt|;
comment|// setup a SolrZkClient to do some getBaseUrlForNodeName testing
name|String
name|zkDir
init|=
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"zookeeper/server1/data"
decl_stmt|;
name|ZkTestServer
name|server
init|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
decl_stmt|;
try|try
block|{
name|server
operator|.
name|run
argument_list|()
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|tryCleanSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|makeSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|TIMEOUT
argument_list|)
decl_stmt|;
try|try
block|{
comment|// getBaseUrlForNodeName
name|assertEquals
argument_list|(
literal|"http://zzz.xxx:1234/solr"
argument_list|,
name|zkClient
operator|.
name|getBaseUrlForNodeName
argument_list|(
literal|"zzz.xxx:1234_solr"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://xxx:99"
argument_list|,
name|zkClient
operator|.
name|getBaseUrlForNodeName
argument_list|(
literal|"xxx:99_"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://foo-bar.baz.org:9999/some_dir"
argument_list|,
name|zkClient
operator|.
name|getBaseUrlForNodeName
argument_list|(
literal|"foo-bar.baz.org:9999_some_dir"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://foo-bar.baz.org:9999/solr/sub_dir"
argument_list|,
name|zkClient
operator|.
name|getBaseUrlForNodeName
argument_list|(
literal|"foo-bar.baz.org:9999_solr%2Fsub_dir"
argument_list|)
argument_list|)
expr_stmt|;
comment|// generateNodeName + getBaseUrlForNodeName
name|assertEquals
argument_list|(
literal|"http://foo:9876/solr"
argument_list|,
name|zkClient
operator|.
name|getBaseUrlForNodeName
argument_list|(
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"foo"
argument_list|,
literal|"9876"
argument_list|,
literal|"solr"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://foo:9876/solr"
argument_list|,
name|zkClient
operator|.
name|getBaseUrlForNodeName
argument_list|(
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"foo"
argument_list|,
literal|"9876"
argument_list|,
literal|"/solr"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://foo:9876/solr"
argument_list|,
name|zkClient
operator|.
name|getBaseUrlForNodeName
argument_list|(
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"foo"
argument_list|,
literal|"9876"
argument_list|,
literal|"/solr/"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://foo.bar.com:9876/solr/sub_dir"
argument_list|,
name|zkClient
operator|.
name|getBaseUrlForNodeName
argument_list|(
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"foo.bar.com"
argument_list|,
literal|"9876"
argument_list|,
literal|"solr/sub_dir"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://foo.bar.com:9876/solr/sub_dir"
argument_list|,
name|zkClient
operator|.
name|getBaseUrlForNodeName
argument_list|(
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"foo.bar.com"
argument_list|,
literal|"9876"
argument_list|,
literal|"/solr/sub_dir/"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://foo-bar:9876"
argument_list|,
name|zkClient
operator|.
name|getBaseUrlForNodeName
argument_list|(
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"foo-bar"
argument_list|,
literal|"9876"
argument_list|,
literal|""
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://foo-bar:9876"
argument_list|,
name|zkClient
operator|.
name|getBaseUrlForNodeName
argument_list|(
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"foo-bar"
argument_list|,
literal|"9876"
argument_list|,
literal|"/"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://foo-bar.com:80/some_dir"
argument_list|,
name|zkClient
operator|.
name|getBaseUrlForNodeName
argument_list|(
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"foo-bar.com"
argument_list|,
literal|"80"
argument_list|,
literal|"some_dir"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://foo-bar.com:80/some_dir"
argument_list|,
name|zkClient
operator|.
name|getBaseUrlForNodeName
argument_list|(
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"foo-bar.com"
argument_list|,
literal|"80"
argument_list|,
literal|"/some_dir"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testReadConfigName
specifier|public
name|void
name|testReadConfigName
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|zkDir
init|=
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"zookeeper/server1/data"
decl_stmt|;
name|CoreContainer
name|cc
init|=
literal|null
decl_stmt|;
name|ZkTestServer
name|server
init|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
decl_stmt|;
try|try
block|{
name|server
operator|.
name|run
argument_list|()
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|tryCleanSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|makeSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|TIMEOUT
argument_list|)
decl_stmt|;
name|String
name|actualConfigName
init|=
literal|"firstConfig"
decl_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
name|ZkController
operator|.
name|CONFIGS_ZKNODE
operator|+
literal|"/"
operator|+
name|actualConfigName
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"configName"
argument_list|,
name|actualConfigName
argument_list|)
expr_stmt|;
name|ZkNodeProps
name|zkProps
init|=
operator|new
name|ZkNodeProps
argument_list|(
name|props
argument_list|)
decl_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
name|ZkStateReader
operator|.
name|COLLECTIONS_ZKNODE
operator|+
literal|"/"
operator|+
name|COLLECTION_NAME
argument_list|,
name|ZkStateReader
operator|.
name|toJSON
argument_list|(
name|zkProps
argument_list|)
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|DEBUG
condition|)
block|{
name|zkClient
operator|.
name|printLayoutToStdOut
argument_list|()
expr_stmt|;
block|}
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
name|cc
operator|=
name|getCoreContainer
argument_list|()
expr_stmt|;
name|ZkController
name|zkController
init|=
operator|new
name|ZkController
argument_list|(
name|cc
argument_list|,
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|TIMEOUT
argument_list|,
literal|10000
argument_list|,
literal|"127.0.0.1"
argument_list|,
literal|"8983"
argument_list|,
literal|"solr"
argument_list|,
literal|"0"
argument_list|,
literal|true
argument_list|,
literal|10000
argument_list|,
literal|10000
argument_list|,
operator|new
name|CurrentCoreDescriptorProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|CoreDescriptor
argument_list|>
name|getCurrentDescriptors
parameter_list|()
block|{
comment|// do nothing
return|return
literal|null
return|;
block|}
block|}
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|configName
init|=
name|zkController
operator|.
name|readConfigName
argument_list|(
name|COLLECTION_NAME
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|configName
argument_list|,
name|actualConfigName
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|zkController
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|cc
operator|!=
literal|null
condition|)
block|{
name|cc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testUploadToCloud
specifier|public
name|void
name|testUploadToCloud
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|zkDir
init|=
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"zookeeper/server1/data"
decl_stmt|;
name|ZkTestServer
name|server
init|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
decl_stmt|;
name|ZkController
name|zkController
init|=
literal|null
decl_stmt|;
name|boolean
name|testFinished
init|=
literal|false
decl_stmt|;
name|CoreContainer
name|cc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|server
operator|.
name|run
argument_list|()
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|makeSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|cc
operator|=
name|getCoreContainer
argument_list|()
expr_stmt|;
name|zkController
operator|=
operator|new
name|ZkController
argument_list|(
name|cc
argument_list|,
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|TIMEOUT
argument_list|,
literal|10000
argument_list|,
literal|"127.0.0.1"
argument_list|,
literal|"8983"
argument_list|,
literal|"solr"
argument_list|,
literal|"0"
argument_list|,
literal|true
argument_list|,
literal|10000
argument_list|,
literal|10000
argument_list|,
operator|new
name|CurrentCoreDescriptorProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|CoreDescriptor
argument_list|>
name|getCurrentDescriptors
parameter_list|()
block|{
comment|// do nothing
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|zkController
operator|.
name|uploadToZK
argument_list|(
operator|new
name|File
argument_list|(
name|ExternalPaths
operator|.
name|EXAMPLE_HOME
operator|+
literal|"/collection1/conf"
argument_list|)
argument_list|,
name|ZkController
operator|.
name|CONFIGS_ZKNODE
operator|+
literal|"/config1"
argument_list|)
expr_stmt|;
comment|// uploading again should overwrite, not error...
name|zkController
operator|.
name|uploadToZK
argument_list|(
operator|new
name|File
argument_list|(
name|ExternalPaths
operator|.
name|EXAMPLE_HOME
operator|+
literal|"/collection1/conf"
argument_list|)
argument_list|,
name|ZkController
operator|.
name|CONFIGS_ZKNODE
operator|+
literal|"/config1"
argument_list|)
expr_stmt|;
if|if
condition|(
name|DEBUG
condition|)
block|{
name|zkController
operator|.
name|printLayoutToStdOut
argument_list|()
expr_stmt|;
block|}
name|testFinished
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|testFinished
operator|&
name|zkController
operator|!=
literal|null
condition|)
block|{
name|zkController
operator|.
name|getZkClient
argument_list|()
operator|.
name|printLayoutToStdOut
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|zkController
operator|!=
literal|null
condition|)
block|{
name|zkController
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|cc
operator|!=
literal|null
condition|)
block|{
name|cc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getCoreContainer
specifier|private
name|CoreContainer
name|getCoreContainer
parameter_list|()
block|{
name|CoreContainer
name|cc
init|=
operator|new
name|CoreContainer
argument_list|(
name|TEMP_DIR
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|cc
operator|.
name|load
argument_list|()
expr_stmt|;
return|return
name|cc
return|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
