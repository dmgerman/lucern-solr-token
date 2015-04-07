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
name|io
operator|.
name|InputStream
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
name|net
operator|.
name|URLEncoder
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|DirectoryStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|lucene
operator|.
name|index
operator|.
name|IndexFileNames
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
name|TestUtil
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
name|embedded
operator|.
name|JettySolrRunner
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
name|response
operator|.
name|QueryResponse
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
name|SolrInputDocument
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
name|solr
operator|.
name|util
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
comment|// Currently unknown why SSL does not work with this test
DECL|class|TestRestoreCore
specifier|public
class|class
name|TestRestoreCore
extends|extends
name|SolrJettyTestBase
block|{
DECL|field|masterJetty
name|JettySolrRunner
name|masterJetty
decl_stmt|;
DECL|field|master
name|TestReplicationHandler
operator|.
name|SolrInstance
name|master
init|=
literal|null
decl_stmt|;
DECL|field|masterClient
name|SolrClient
name|masterClient
decl_stmt|;
DECL|field|CONF_DIR
specifier|private
specifier|static
specifier|final
name|String
name|CONF_DIR
init|=
literal|"solr"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"collection1"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"conf"
operator|+
name|File
operator|.
name|separator
decl_stmt|;
DECL|field|context
specifier|private
specifier|static
name|String
name|context
init|=
literal|"/solr"
decl_stmt|;
DECL|method|createJetty
specifier|private
specifier|static
name|JettySolrRunner
name|createJetty
parameter_list|(
name|TestReplicationHandler
operator|.
name|SolrInstance
name|instance
parameter_list|)
throws|throws
name|Exception
block|{
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
argument_list|,
literal|"solr.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|instance
operator|.
name|getHomeDir
argument_list|()
argument_list|,
literal|"solr.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|JettySolrRunner
name|jetty
init|=
operator|new
name|JettySolrRunner
argument_list|(
name|instance
operator|.
name|getHomeDir
argument_list|()
argument_list|,
literal|"/solr"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|jetty
operator|.
name|setDataDir
argument_list|(
name|instance
operator|.
name|getDataDir
argument_list|()
argument_list|)
expr_stmt|;
name|jetty
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|jetty
return|;
block|}
DECL|method|createNewSolrClient
specifier|private
specifier|static
name|SolrClient
name|createNewSolrClient
parameter_list|(
name|int
name|port
parameter_list|)
block|{
try|try
block|{
comment|// setup the client...
name|HttpSolrClient
name|client
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|buildUrl
argument_list|(
name|port
argument_list|,
name|context
argument_list|)
operator|+
literal|"/"
operator|+
name|DEFAULT_TEST_CORENAME
argument_list|)
decl_stmt|;
name|client
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|client
operator|.
name|setSoTimeout
argument_list|(
literal|60000
argument_list|)
expr_stmt|;
name|client
operator|.
name|setDefaultMaxConnectionsPerHost
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|client
operator|.
name|setMaxTotalConnections
argument_list|(
literal|100
argument_list|)
expr_stmt|;
return|return
name|client
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
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
name|String
name|configFile
init|=
literal|"solrconfig-master.xml"
decl_stmt|;
name|master
operator|=
operator|new
name|TestReplicationHandler
operator|.
name|SolrInstance
argument_list|(
name|createTempDir
argument_list|(
literal|"solr-instance"
argument_list|)
operator|.
name|toFile
argument_list|()
argument_list|,
literal|"master"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|master
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|master
operator|.
name|copyConfigFile
argument_list|(
name|CONF_DIR
operator|+
name|configFile
argument_list|,
literal|"solrconfig.xml"
argument_list|)
expr_stmt|;
name|masterJetty
operator|=
name|createJetty
argument_list|(
name|master
argument_list|)
expr_stmt|;
name|masterClient
operator|=
name|createNewSolrClient
argument_list|(
name|masterJetty
operator|.
name|getLocalPort
argument_list|()
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|masterClient
operator|.
name|close
argument_list|()
expr_stmt|;
name|masterClient
operator|=
literal|null
expr_stmt|;
name|masterJetty
operator|.
name|stop
argument_list|()
expr_stmt|;
name|masterJetty
operator|=
literal|null
expr_stmt|;
name|master
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimpleRestore
specifier|public
name|void
name|testSimpleRestore
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|nDocs
init|=
name|TestReplicationHandlerBackup
operator|.
name|indexDocs
argument_list|(
name|masterClient
argument_list|)
decl_stmt|;
name|String
name|snapshotName
decl_stmt|;
name|String
name|location
decl_stmt|;
name|String
name|params
init|=
literal|""
decl_stmt|;
comment|//Use the default backup location or an externally provided location.
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|location
operator|=
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|params
operator|+=
literal|"&location="
operator|+
name|URLEncoder
operator|.
name|encode
argument_list|(
name|location
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
comment|//named snapshot vs default snapshot name
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|snapshotName
operator|=
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|params
operator|+=
literal|"&name="
operator|+
name|snapshotName
expr_stmt|;
block|}
name|TestReplicationHandlerBackup
operator|.
name|runBackupCommand
argument_list|(
name|masterJetty
argument_list|,
name|ReplicationHandler
operator|.
name|CMD_BACKUP
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|CheckBackupStatus
name|checkBackupStatus
init|=
operator|new
name|CheckBackupStatus
argument_list|(
operator|(
name|HttpSolrClient
operator|)
name|masterClient
argument_list|,
literal|null
argument_list|)
decl_stmt|;
while|while
condition|(
operator|!
name|checkBackupStatus
operator|.
name|success
condition|)
block|{
name|checkBackupStatus
operator|.
name|fetchStatus
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
comment|//Modify existing index before we call restore.
comment|//Delete a few docs
name|int
name|numDeletes
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|nDocs
argument_list|)
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
name|numDeletes
condition|;
name|i
operator|++
control|)
block|{
name|masterClient
operator|.
name|deleteByQuery
argument_list|(
literal|"id:"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|masterClient
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|//Add a few more
name|int
name|moreAdds
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|)
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
name|moreAdds
condition|;
name|i
operator|++
control|)
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
name|i
operator|+
name|nDocs
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"name"
argument_list|,
literal|"name = "
operator|+
operator|(
name|i
operator|+
name|nDocs
operator|)
argument_list|)
expr_stmt|;
name|masterClient
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
comment|//Purposely not calling commit once in a while. There can be some docs which are not committed
if|if
condition|(
name|usually
argument_list|()
condition|)
block|{
name|masterClient
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|TestReplicationHandlerBackup
operator|.
name|runBackupCommand
argument_list|(
name|masterJetty
argument_list|,
name|ReplicationHandler
operator|.
name|CMD_RESTORE
argument_list|,
name|params
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|fetchRestoreStatus
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
comment|//See if restore was successful by checking if all the docs are present again
name|verifyDocs
argument_list|(
name|nDocs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailedRestore
specifier|public
name|void
name|testFailedRestore
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|nDocs
init|=
name|TestReplicationHandlerBackup
operator|.
name|indexDocs
argument_list|(
name|masterClient
argument_list|)
decl_stmt|;
name|String
name|location
init|=
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|String
name|snapshotName
init|=
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|String
name|params
init|=
literal|"&name="
operator|+
name|snapshotName
operator|+
literal|"&location="
operator|+
name|URLEncoder
operator|.
name|encode
argument_list|(
name|location
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|TestReplicationHandlerBackup
operator|.
name|runBackupCommand
argument_list|(
name|masterJetty
argument_list|,
name|ReplicationHandler
operator|.
name|CMD_BACKUP
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|CheckBackupStatus
name|checkBackupStatus
init|=
operator|new
name|CheckBackupStatus
argument_list|(
operator|(
name|HttpSolrClient
operator|)
name|masterClient
argument_list|,
literal|null
argument_list|)
decl_stmt|;
while|while
condition|(
operator|!
name|checkBackupStatus
operator|.
name|success
condition|)
block|{
name|checkBackupStatus
operator|.
name|fetchStatus
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
comment|//Remove the segments_n file so that the backup index is corrupted.
comment|//Restore should fail and it should automatically rollback to the original index.
name|Path
name|restoreIndexPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|location
argument_list|,
literal|"snapshot."
operator|+
name|snapshotName
argument_list|)
decl_stmt|;
try|try
init|(
name|DirectoryStream
argument_list|<
name|Path
argument_list|>
name|stream
init|=
name|Files
operator|.
name|newDirectoryStream
argument_list|(
name|restoreIndexPath
argument_list|,
name|IndexFileNames
operator|.
name|SEGMENTS
operator|+
literal|"*"
argument_list|)
init|)
block|{
name|Path
name|segmentFileName
init|=
name|stream
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|Files
operator|.
name|delete
argument_list|(
name|segmentFileName
argument_list|)
expr_stmt|;
block|}
name|TestReplicationHandlerBackup
operator|.
name|runBackupCommand
argument_list|(
name|masterJetty
argument_list|,
name|ReplicationHandler
operator|.
name|CMD_RESTORE
argument_list|,
name|params
argument_list|)
expr_stmt|;
try|try
block|{
while|while
condition|(
operator|!
name|fetchRestoreStatus
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
name|fail
argument_list|(
literal|"Should have thrown an error because restore could not have been successful"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{
comment|//supposed to happen
block|}
name|verifyDocs
argument_list|(
name|nDocs
argument_list|)
expr_stmt|;
comment|//make sure we can write to the index again
name|nDocs
operator|=
name|TestReplicationHandlerBackup
operator|.
name|indexDocs
argument_list|(
name|masterClient
argument_list|)
expr_stmt|;
name|verifyDocs
argument_list|(
name|nDocs
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyDocs
specifier|private
name|void
name|verifyDocs
parameter_list|(
name|int
name|nDocs
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|ModifiableSolrParams
name|queryParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|queryParams
operator|.
name|set
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|QueryResponse
name|response
init|=
name|masterClient
operator|.
name|query
argument_list|(
name|queryParams
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|nDocs
argument_list|,
name|response
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|fetchRestoreStatus
specifier|private
name|boolean
name|fetchRestoreStatus
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|masterUrl
init|=
name|buildUrl
argument_list|(
name|masterJetty
operator|.
name|getLocalPort
argument_list|()
argument_list|,
name|context
argument_list|)
operator|+
literal|"/"
operator|+
name|DEFAULT_TEST_CORENAME
operator|+
literal|"/replication?command="
operator|+
name|ReplicationHandler
operator|.
name|CMD_RESTORE_STATUS
decl_stmt|;
specifier|final
name|Pattern
name|pException
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"<str name=\"exception\">(.*?)</str>"
argument_list|)
decl_stmt|;
name|InputStream
name|stream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|masterUrl
argument_list|)
decl_stmt|;
name|stream
operator|=
name|url
operator|.
name|openStream
argument_list|()
expr_stmt|;
name|String
name|response
init|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|stream
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|Matcher
name|matcher
init|=
name|pException
operator|.
name|matcher
argument_list|(
name|response
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"Failed to complete restore action with exception "
operator|+
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|response
operator|.
name|contains
argument_list|(
literal|"<str name=\"status\">success</str>"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|response
operator|.
name|contains
argument_list|(
literal|"<str name=\"status\">failed</str>"
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Restore Failed"
argument_list|)
expr_stmt|;
block|}
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class
end_unit
