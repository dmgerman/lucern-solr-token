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
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|text
operator|.
name|SimpleDateFormat
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
name|Collection
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
name|Date
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
name|Locale
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
name|IndexCommit
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
name|store
operator|.
name|Lock
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
name|store
operator|.
name|SimpleFSLockFactory
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
name|core
operator|.
name|IndexDeletionPolicyWrapper
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
begin_comment
comment|/**  *<p/> Provides functionality equivalent to the snapshooter script</p>  *  *  * @since solr 1.4  */
end_comment
begin_class
DECL|class|SnapShooter
specifier|public
class|class
name|SnapShooter
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SnapShooter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|snapDir
specifier|private
name|String
name|snapDir
init|=
literal|null
decl_stmt|;
DECL|field|solrCore
specifier|private
name|SolrCore
name|solrCore
decl_stmt|;
DECL|field|lockFactory
specifier|private
name|SimpleFSLockFactory
name|lockFactory
decl_stmt|;
DECL|method|SnapShooter
specifier|public
name|SnapShooter
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|String
name|location
parameter_list|)
block|{
name|solrCore
operator|=
name|core
expr_stmt|;
if|if
condition|(
name|location
operator|==
literal|null
condition|)
name|snapDir
operator|=
name|core
operator|.
name|getDataDir
argument_list|()
expr_stmt|;
else|else
block|{
name|File
name|base
init|=
operator|new
name|File
argument_list|(
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getInstanceDir
argument_list|()
argument_list|)
decl_stmt|;
name|snapDir
operator|=
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|FileUtils
operator|.
name|resolvePath
argument_list|(
name|base
argument_list|,
name|location
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|snapDir
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dir
operator|.
name|exists
argument_list|()
condition|)
name|dir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
name|lockFactory
operator|=
operator|new
name|SimpleFSLockFactory
argument_list|(
name|snapDir
argument_list|)
expr_stmt|;
block|}
DECL|method|createSnapAsync
name|void
name|createSnapAsync
parameter_list|(
specifier|final
name|IndexCommit
name|indexCommit
parameter_list|,
specifier|final
name|ReplicationHandler
name|replicationHandler
parameter_list|)
block|{
name|createSnapAsync
argument_list|(
name|indexCommit
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|replicationHandler
argument_list|)
expr_stmt|;
block|}
DECL|method|createSnapAsync
name|void
name|createSnapAsync
parameter_list|(
specifier|final
name|IndexCommit
name|indexCommit
parameter_list|,
specifier|final
name|int
name|numberToKeep
parameter_list|,
specifier|final
name|ReplicationHandler
name|replicationHandler
parameter_list|)
block|{
name|replicationHandler
operator|.
name|core
operator|.
name|getDeletionPolicy
argument_list|()
operator|.
name|saveCommitPoint
argument_list|(
name|indexCommit
operator|.
name|getGeneration
argument_list|()
argument_list|)
expr_stmt|;
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|createSnapshot
argument_list|(
name|indexCommit
argument_list|,
name|numberToKeep
argument_list|,
name|replicationHandler
argument_list|)
expr_stmt|;
block|}
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|createSnapshot
name|void
name|createSnapshot
parameter_list|(
specifier|final
name|IndexCommit
name|indexCommit
parameter_list|,
name|int
name|numberToKeep
parameter_list|,
name|ReplicationHandler
name|replicationHandler
parameter_list|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|details
init|=
operator|new
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|details
operator|.
name|add
argument_list|(
literal|"startTime"
argument_list|,
operator|new
name|Date
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|snapShotDir
init|=
literal|null
decl_stmt|;
name|String
name|directoryName
init|=
literal|null
decl_stmt|;
name|Lock
name|lock
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|numberToKeep
operator|<
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|deleteOldBackups
argument_list|(
name|numberToKeep
argument_list|)
expr_stmt|;
block|}
name|SimpleDateFormat
name|fmt
init|=
operator|new
name|SimpleDateFormat
argument_list|(
name|DATE_FMT
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|directoryName
operator|=
literal|"snapshot."
operator|+
name|fmt
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
expr_stmt|;
name|lock
operator|=
name|lockFactory
operator|.
name|makeLock
argument_list|(
name|directoryName
operator|+
literal|".lock"
argument_list|)
expr_stmt|;
if|if
condition|(
name|lock
operator|.
name|isLocked
argument_list|()
condition|)
return|return;
name|snapShotDir
operator|=
operator|new
name|File
argument_list|(
name|snapDir
argument_list|,
name|directoryName
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|snapShotDir
operator|.
name|mkdir
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to create snapshot directory: "
operator|+
name|snapShotDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|Collection
argument_list|<
name|String
argument_list|>
name|files
init|=
name|indexCommit
operator|.
name|getFileNames
argument_list|()
decl_stmt|;
name|FileCopier
name|fileCopier
init|=
operator|new
name|FileCopier
argument_list|(
name|solrCore
operator|.
name|getDeletionPolicy
argument_list|()
argument_list|,
name|indexCommit
argument_list|)
decl_stmt|;
name|fileCopier
operator|.
name|copyFiles
argument_list|(
name|files
argument_list|,
name|snapShotDir
argument_list|)
expr_stmt|;
name|details
operator|.
name|add
argument_list|(
literal|"fileCount"
argument_list|,
name|files
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|details
operator|.
name|add
argument_list|(
literal|"status"
argument_list|,
literal|"success"
argument_list|)
expr_stmt|;
name|details
operator|.
name|add
argument_list|(
literal|"snapshotCompletedAt"
argument_list|,
operator|new
name|Date
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|SnapPuller
operator|.
name|delTree
argument_list|(
name|snapShotDir
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception while creating snapshot"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|details
operator|.
name|add
argument_list|(
literal|"snapShootException"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|replicationHandler
operator|.
name|core
operator|.
name|getDeletionPolicy
argument_list|()
operator|.
name|releaseCommitPoint
argument_list|(
name|indexCommit
operator|.
name|getGeneration
argument_list|()
argument_list|)
expr_stmt|;
name|replicationHandler
operator|.
name|snapShootDetails
operator|=
name|details
expr_stmt|;
if|if
condition|(
name|lock
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|lock
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to release snapshoot lock: "
operator|+
name|directoryName
operator|+
literal|".lock"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|deleteOldBackups
specifier|private
name|void
name|deleteOldBackups
parameter_list|(
name|int
name|numberToKeep
parameter_list|)
block|{
name|File
index|[]
name|files
init|=
operator|new
name|File
argument_list|(
name|snapDir
argument_list|)
operator|.
name|listFiles
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|OldBackupDirectory
argument_list|>
name|dirs
init|=
operator|new
name|ArrayList
argument_list|<
name|OldBackupDirectory
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|f
range|:
name|files
control|)
block|{
name|OldBackupDirectory
name|obd
init|=
operator|new
name|OldBackupDirectory
argument_list|(
name|f
argument_list|)
decl_stmt|;
if|if
condition|(
name|obd
operator|.
name|dir
operator|!=
literal|null
condition|)
block|{
name|dirs
operator|.
name|add
argument_list|(
name|obd
argument_list|)
expr_stmt|;
block|}
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|dirs
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|1
decl_stmt|;
for|for
control|(
name|OldBackupDirectory
name|dir
range|:
name|dirs
control|)
block|{
if|if
condition|(
name|i
operator|++
operator|>
name|numberToKeep
operator|-
literal|1
condition|)
block|{
name|SnapPuller
operator|.
name|delTree
argument_list|(
name|dir
operator|.
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|OldBackupDirectory
specifier|private
class|class
name|OldBackupDirectory
implements|implements
name|Comparable
argument_list|<
name|OldBackupDirectory
argument_list|>
block|{
DECL|field|dir
name|File
name|dir
decl_stmt|;
DECL|field|timestamp
name|Date
name|timestamp
decl_stmt|;
DECL|field|dirNamePattern
specifier|final
name|Pattern
name|dirNamePattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^snapshot[.](.*)$"
argument_list|)
decl_stmt|;
DECL|method|OldBackupDirectory
name|OldBackupDirectory
parameter_list|(
name|File
name|dir
parameter_list|)
block|{
if|if
condition|(
name|dir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|Matcher
name|m
init|=
name|dirNamePattern
operator|.
name|matcher
argument_list|(
name|dir
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|find
argument_list|()
condition|)
block|{
try|try
block|{
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|this
operator|.
name|timestamp
operator|=
operator|new
name|SimpleDateFormat
argument_list|(
name|DATE_FMT
argument_list|)
operator|.
name|parse
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|1
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
name|this
operator|.
name|dir
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|timestamp
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|OldBackupDirectory
name|that
parameter_list|)
block|{
return|return
name|that
operator|.
name|timestamp
operator|.
name|compareTo
argument_list|(
name|this
operator|.
name|timestamp
argument_list|)
return|;
block|}
block|}
DECL|field|SNAP_DIR
specifier|public
specifier|static
specifier|final
name|String
name|SNAP_DIR
init|=
literal|"snapDir"
decl_stmt|;
DECL|field|DATE_FMT
specifier|public
specifier|static
specifier|final
name|String
name|DATE_FMT
init|=
literal|"yyyyMMddHHmmssSSS"
decl_stmt|;
DECL|class|FileCopier
specifier|private
class|class
name|FileCopier
block|{
DECL|field|DEFAULT_BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_BUFFER_SIZE
init|=
literal|32768
decl_stmt|;
DECL|field|buffer
specifier|private
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|DEFAULT_BUFFER_SIZE
index|]
decl_stmt|;
DECL|field|indexCommit
specifier|private
name|IndexCommit
name|indexCommit
decl_stmt|;
DECL|field|delPolicy
specifier|private
name|IndexDeletionPolicyWrapper
name|delPolicy
decl_stmt|;
DECL|method|FileCopier
specifier|public
name|FileCopier
parameter_list|(
name|IndexDeletionPolicyWrapper
name|delPolicy
parameter_list|,
name|IndexCommit
name|commit
parameter_list|)
block|{
name|this
operator|.
name|delPolicy
operator|=
name|delPolicy
expr_stmt|;
name|this
operator|.
name|indexCommit
operator|=
name|commit
expr_stmt|;
block|}
DECL|method|copyFiles
specifier|public
name|void
name|copyFiles
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|files
parameter_list|,
name|File
name|destDir
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|indexFile
range|:
name|files
control|)
block|{
name|File
name|source
init|=
operator|new
name|File
argument_list|(
name|solrCore
operator|.
name|getIndexDir
argument_list|()
argument_list|,
name|indexFile
argument_list|)
decl_stmt|;
name|copyFile
argument_list|(
name|source
argument_list|,
operator|new
name|File
argument_list|(
name|destDir
argument_list|,
name|source
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|copyFile
specifier|public
name|void
name|copyFile
parameter_list|(
name|File
name|source
parameter_list|,
name|File
name|destination
parameter_list|,
name|boolean
name|preserveFileDate
parameter_list|)
throws|throws
name|IOException
block|{
comment|// check source exists
if|if
condition|(
operator|!
name|source
operator|.
name|exists
argument_list|()
condition|)
block|{
name|String
name|message
init|=
literal|"File "
operator|+
name|source
operator|+
literal|" does not exist"
decl_stmt|;
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|message
argument_list|)
throw|;
block|}
comment|// does destinations directory exist ?
if|if
condition|(
name|destination
operator|.
name|getParentFile
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|destination
operator|.
name|getParentFile
argument_list|()
operator|.
name|exists
argument_list|()
condition|)
block|{
name|destination
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
comment|// make sure we can write to destination
if|if
condition|(
name|destination
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|destination
operator|.
name|canWrite
argument_list|()
condition|)
block|{
name|String
name|message
init|=
literal|"Unable to open file "
operator|+
name|destination
operator|+
literal|" for writing."
decl_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|message
argument_list|)
throw|;
block|}
name|FileInputStream
name|input
init|=
literal|null
decl_stmt|;
name|FileOutputStream
name|output
init|=
literal|null
decl_stmt|;
try|try
block|{
name|input
operator|=
operator|new
name|FileInputStream
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|output
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|int
name|n
init|=
literal|0
decl_stmt|;
name|int
name|rcnt
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|-
literal|1
operator|!=
operator|(
name|n
operator|=
name|input
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
condition|)
block|{
name|output
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|count
operator|+=
name|n
expr_stmt|;
name|rcnt
operator|++
expr_stmt|;
comment|/***           // reserve every 4.6875 MB           if (rcnt == 150) {             rcnt = 0;             delPolicy.setReserveDuration(indexCommit.getVersion(), reserveTime);           }            ***/
block|}
block|}
finally|finally
block|{
try|try
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|output
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|source
operator|.
name|length
argument_list|()
operator|!=
name|destination
operator|.
name|length
argument_list|()
condition|)
block|{
name|String
name|message
init|=
literal|"Failed to copy full contents from "
operator|+
name|source
operator|+
literal|" to "
operator|+
name|destination
decl_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|message
argument_list|)
throw|;
block|}
if|if
condition|(
name|preserveFileDate
condition|)
block|{
comment|// file copy should preserve file date
name|destination
operator|.
name|setLastModified
argument_list|(
name|source
operator|.
name|lastModified
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
