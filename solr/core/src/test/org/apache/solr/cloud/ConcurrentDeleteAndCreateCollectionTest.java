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
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
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
operator|.
name|Nightly
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
name|SolrQuery
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
name|CollectionAdminRequest
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
name|response
operator|.
name|CollectionAdminResponse
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
name|KeeperException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
begin_class
annotation|@
name|Nightly
DECL|class|ConcurrentDeleteAndCreateCollectionTest
specifier|public
class|class
name|ConcurrentDeleteAndCreateCollectionTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|solrCluster
specifier|private
name|MiniSolrCloudCluster
name|solrCluster
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
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
specifier|final
name|File
name|solrXml
init|=
name|getFile
argument_list|(
literal|"solr"
argument_list|)
operator|.
name|toPath
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"solr.xml"
argument_list|)
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|solrCluster
operator|=
operator|new
name|MiniSolrCloudCluster
argument_list|(
literal|1
argument_list|,
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
argument_list|,
name|solrXml
argument_list|,
name|buildJettyConfig
argument_list|(
literal|"/solr"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|solrCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testConcurrentCreateAndDeleteDoesNotFail
specifier|public
name|void
name|testConcurrentCreateAndDeleteDoesNotFail
parameter_list|()
block|{
specifier|final
name|File
name|configDir
init|=
name|getFile
argument_list|(
literal|"solr"
argument_list|)
operator|.
name|toPath
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"configsets/configset-2/conf"
argument_list|)
operator|.
name|toFile
argument_list|()
decl_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|Exception
argument_list|>
name|failure
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|timeToRunSec
init|=
literal|30
decl_stmt|;
specifier|final
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
literal|10
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|collectionName
init|=
literal|"collection"
operator|+
name|i
decl_stmt|;
name|uploadConfig
argument_list|(
name|configDir
argument_list|,
name|collectionName
argument_list|)
expr_stmt|;
specifier|final
name|SolrClient
name|solrClient
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|solrCluster
operator|.
name|getJettySolrRunners
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|CreateDeleteSearchCollectionThread
argument_list|(
literal|"create-delete-search-"
operator|+
name|i
argument_list|,
name|collectionName
argument_list|,
name|collectionName
argument_list|,
name|timeToRunSec
argument_list|,
name|solrClient
argument_list|,
name|failure
argument_list|)
expr_stmt|;
block|}
name|startAll
argument_list|(
name|threads
argument_list|)
expr_stmt|;
name|joinAll
argument_list|(
name|threads
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"concurrent create and delete collection failed: "
operator|+
name|failure
operator|.
name|get
argument_list|()
argument_list|,
name|failure
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testConcurrentCreateAndDeleteOverTheSameConfig
specifier|public
name|void
name|testConcurrentCreateAndDeleteOverTheSameConfig
parameter_list|()
block|{
specifier|final
name|String
name|configName
init|=
literal|"testconfig"
decl_stmt|;
specifier|final
name|File
name|configDir
init|=
name|getFile
argument_list|(
literal|"solr"
argument_list|)
operator|.
name|toPath
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"configsets/configset-2/conf"
argument_list|)
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|uploadConfig
argument_list|(
name|configDir
argument_list|,
name|configName
argument_list|)
expr_stmt|;
comment|// upload config once, to be used by all collections
specifier|final
name|SolrClient
name|solrClient
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|solrCluster
operator|.
name|getJettySolrRunners
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|Exception
argument_list|>
name|failure
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|timeToRunSec
init|=
literal|30
decl_stmt|;
specifier|final
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
literal|2
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|collectionName
init|=
literal|"collection"
operator|+
name|i
decl_stmt|;
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|CreateDeleteCollectionThread
argument_list|(
literal|"create-delete-"
operator|+
name|i
argument_list|,
name|collectionName
argument_list|,
name|configName
argument_list|,
name|timeToRunSec
argument_list|,
name|solrClient
argument_list|,
name|failure
argument_list|)
expr_stmt|;
block|}
name|startAll
argument_list|(
name|threads
argument_list|)
expr_stmt|;
name|joinAll
argument_list|(
name|threads
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"concurrent create and delete collection failed: "
operator|+
name|failure
operator|.
name|get
argument_list|()
argument_list|,
name|failure
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|solrClient
operator|.
name|close
argument_list|()
expr_stmt|;
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
DECL|method|uploadConfig
specifier|private
name|void
name|uploadConfig
parameter_list|(
name|File
name|configDir
parameter_list|,
name|String
name|configName
parameter_list|)
block|{
try|try
block|{
name|solrCluster
operator|.
name|uploadConfigDir
argument_list|(
name|configDir
argument_list|,
name|configName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|KeeperException
decl||
name|InterruptedException
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
DECL|method|joinAll
specifier|private
name|void
name|joinAll
parameter_list|(
specifier|final
name|Thread
index|[]
name|threads
parameter_list|)
block|{
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
block|{
try|try
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|interrupted
argument_list|()
expr_stmt|;
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
DECL|method|startAll
specifier|private
name|void
name|startAll
parameter_list|(
specifier|final
name|Thread
index|[]
name|threads
parameter_list|)
block|{
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
block|{
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|CreateDeleteCollectionThread
specifier|private
specifier|static
class|class
name|CreateDeleteCollectionThread
extends|extends
name|Thread
block|{
DECL|field|collectionName
specifier|protected
specifier|final
name|String
name|collectionName
decl_stmt|;
DECL|field|configName
specifier|protected
specifier|final
name|String
name|configName
decl_stmt|;
DECL|field|timeToRunSec
specifier|protected
specifier|final
name|long
name|timeToRunSec
decl_stmt|;
DECL|field|solrClient
specifier|protected
specifier|final
name|SolrClient
name|solrClient
decl_stmt|;
DECL|field|failure
specifier|protected
specifier|final
name|AtomicReference
argument_list|<
name|Exception
argument_list|>
name|failure
decl_stmt|;
DECL|method|CreateDeleteCollectionThread
specifier|public
name|CreateDeleteCollectionThread
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|collectionName
parameter_list|,
name|String
name|configName
parameter_list|,
name|long
name|timeToRunSec
parameter_list|,
name|SolrClient
name|solrClient
parameter_list|,
name|AtomicReference
argument_list|<
name|Exception
argument_list|>
name|failure
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|collectionName
operator|=
name|collectionName
expr_stmt|;
name|this
operator|.
name|timeToRunSec
operator|=
name|timeToRunSec
expr_stmt|;
name|this
operator|.
name|solrClient
operator|=
name|solrClient
expr_stmt|;
name|this
operator|.
name|failure
operator|=
name|failure
expr_stmt|;
name|this
operator|.
name|configName
operator|=
name|configName
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
specifier|final
name|long
name|timeToStop
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
name|timeToRunSec
argument_list|)
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|timeToStop
operator|&&
name|failure
operator|.
name|get
argument_list|()
operator|==
literal|null
condition|)
block|{
name|doWork
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|doWork
specifier|protected
name|void
name|doWork
parameter_list|()
block|{
name|createCollection
argument_list|()
expr_stmt|;
name|deleteCollection
argument_list|()
expr_stmt|;
block|}
DECL|method|addFailure
specifier|protected
name|void
name|addFailure
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
synchronized|synchronized
init|(
name|failure
init|)
block|{
if|if
condition|(
name|failure
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|failure
operator|.
name|get
argument_list|()
operator|.
name|addSuppressed
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|failure
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|createCollection
specifier|private
name|void
name|createCollection
parameter_list|()
block|{
try|try
block|{
specifier|final
name|CollectionAdminResponse
name|response
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|Create
argument_list|()
operator|.
name|setCollectionName
argument_list|(
name|collectionName
argument_list|)
operator|.
name|setNumShards
argument_list|(
literal|1
argument_list|)
operator|.
name|setReplicationFactor
argument_list|(
literal|1
argument_list|)
operator|.
name|setConfigName
argument_list|(
name|configName
argument_list|)
operator|.
name|process
argument_list|(
name|solrClient
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getStatus
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|addFailure
argument_list|(
operator|new
name|RuntimeException
argument_list|(
literal|"failed to create collection "
operator|+
name|collectionName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|addFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|deleteCollection
specifier|private
name|void
name|deleteCollection
parameter_list|()
block|{
try|try
block|{
specifier|final
name|CollectionAdminRequest
operator|.
name|Delete
name|deleteCollectionRequest
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|Delete
argument_list|()
operator|.
name|setCollectionName
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
specifier|final
name|CollectionAdminResponse
name|response
init|=
name|deleteCollectionRequest
operator|.
name|process
argument_list|(
name|solrClient
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getStatus
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|addFailure
argument_list|(
operator|new
name|RuntimeException
argument_list|(
literal|"failed to delete collection "
operator|+
name|collectionName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|addFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|CreateDeleteSearchCollectionThread
specifier|private
specifier|static
class|class
name|CreateDeleteSearchCollectionThread
extends|extends
name|CreateDeleteCollectionThread
block|{
DECL|method|CreateDeleteSearchCollectionThread
specifier|public
name|CreateDeleteSearchCollectionThread
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|collectionName
parameter_list|,
name|String
name|configName
parameter_list|,
name|long
name|timeToRunSec
parameter_list|,
name|SolrClient
name|solrClient
parameter_list|,
name|AtomicReference
argument_list|<
name|Exception
argument_list|>
name|failure
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|collectionName
argument_list|,
name|configName
argument_list|,
name|timeToRunSec
argument_list|,
name|solrClient
argument_list|,
name|failure
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doWork
specifier|protected
name|void
name|doWork
parameter_list|()
block|{
name|super
operator|.
name|doWork
argument_list|()
expr_stmt|;
name|searchNonExistingCollection
argument_list|()
expr_stmt|;
block|}
DECL|method|searchNonExistingCollection
specifier|private
name|void
name|searchNonExistingCollection
parameter_list|()
block|{
try|try
block|{
name|solrClient
operator|.
name|query
argument_list|(
name|collectionName
argument_list|,
operator|new
name|SolrQuery
argument_list|(
literal|"*"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"not found"
argument_list|)
operator|&&
operator|!
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Can not find"
argument_list|)
condition|)
block|{
name|addFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class
end_unit
