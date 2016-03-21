begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
package|;
end_package
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
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
name|Arrays
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileStatus
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileSystem
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|PathFilter
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
operator|.
name|RemoteException
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
name|BytesRef
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
name|SolrException
operator|.
name|ErrorCode
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
name|core
operator|.
name|PluginInfo
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
name|HdfsUtil
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
comment|/** @lucene.experimental */
end_comment
begin_class
DECL|class|HdfsUpdateLog
specifier|public
class|class
name|HdfsUpdateLog
extends|extends
name|UpdateLog
block|{
DECL|field|fsLock
specifier|private
specifier|final
name|Object
name|fsLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|field|tlogDir
specifier|private
specifier|volatile
name|Path
name|tlogDir
decl_stmt|;
DECL|field|confDir
specifier|private
specifier|final
name|String
name|confDir
decl_stmt|;
DECL|field|tlogDfsReplication
specifier|private
name|Integer
name|tlogDfsReplication
decl_stmt|;
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|debug
specifier|private
name|boolean
name|debug
init|=
name|log
operator|.
name|isDebugEnabled
argument_list|()
decl_stmt|;
comment|// used internally by tests to track total count of failed tran log loads in init
DECL|field|INIT_FAILED_LOGS_COUNT
specifier|public
specifier|static
name|AtomicLong
name|INIT_FAILED_LOGS_COUNT
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|method|HdfsUpdateLog
specifier|public
name|HdfsUpdateLog
parameter_list|()
block|{
name|this
operator|.
name|confDir
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|HdfsUpdateLog
specifier|public
name|HdfsUpdateLog
parameter_list|(
name|String
name|confDir
parameter_list|)
block|{
name|this
operator|.
name|confDir
operator|=
name|confDir
expr_stmt|;
block|}
comment|// HACK
comment|// while waiting for HDFS-3107, instead of quickly
comment|// dropping, we slowly apply
comment|// This is somewhat brittle, but current usage
comment|// allows for it
annotation|@
name|Override
DECL|method|dropBufferedUpdates
specifier|public
name|boolean
name|dropBufferedUpdates
parameter_list|()
block|{
name|versionInfo
operator|.
name|blockUpdates
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|state
operator|!=
name|State
operator|.
name|BUFFERING
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|log
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Dropping buffered updates "
operator|+
name|this
argument_list|)
expr_stmt|;
block|}
comment|// since we blocked updates, this synchronization shouldn't strictly be
comment|// necessary.
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|tlog
operator|!=
literal|null
condition|)
block|{
comment|// tlog.rollback(recoveryInfo.positionOfStart);
block|}
block|}
name|state
operator|=
name|State
operator|.
name|ACTIVE
expr_stmt|;
name|operationFlags
operator|&=
operator|~
name|FLAG_GAP
expr_stmt|;
block|}
finally|finally
block|{
name|versionInfo
operator|.
name|unblockUpdates
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|PluginInfo
name|info
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|tlogDfsReplication
operator|=
operator|(
name|Integer
operator|)
name|info
operator|.
name|initArgs
operator|.
name|get
argument_list|(
literal|"tlogDfsReplication"
argument_list|)
expr_stmt|;
if|if
condition|(
name|tlogDfsReplication
operator|==
literal|null
condition|)
name|tlogDfsReplication
operator|=
literal|3
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Initializing HdfsUpdateLog: tlogDfsReplication={}"
argument_list|,
name|tlogDfsReplication
argument_list|)
expr_stmt|;
block|}
DECL|method|getConf
specifier|private
name|Configuration
name|getConf
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
if|if
condition|(
name|confDir
operator|!=
literal|null
condition|)
block|{
name|HdfsUtil
operator|.
name|addHdfsResources
argument_list|(
name|conf
argument_list|,
name|confDir
argument_list|)
expr_stmt|;
block|}
name|conf
operator|.
name|setBoolean
argument_list|(
literal|"fs.hdfs.impl.disable.cache"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|UpdateHandler
name|uhandler
parameter_list|,
name|SolrCore
name|core
parameter_list|)
block|{
comment|// ulogDir from CoreDescriptor overrides
name|String
name|ulogDir
init|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getUlogDir
argument_list|()
decl_stmt|;
name|this
operator|.
name|uhandler
operator|=
name|uhandler
expr_stmt|;
synchronized|synchronized
init|(
name|fsLock
init|)
block|{
comment|// just like dataDir, we do not allow
comment|// moving the tlog dir on reload
if|if
condition|(
name|fs
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|ulogDir
operator|!=
literal|null
condition|)
block|{
name|dataDir
operator|=
name|ulogDir
expr_stmt|;
block|}
if|if
condition|(
name|dataDir
operator|==
literal|null
operator|||
name|dataDir
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|dataDir
operator|=
name|core
operator|.
name|getDataDir
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|core
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|isAbsolute
argument_list|(
name|dataDir
argument_list|)
condition|)
block|{
try|try
block|{
name|dataDir
operator|=
name|core
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|getDataHome
argument_list|(
name|core
operator|.
name|getCoreDescriptor
argument_list|()
argument_list|)
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
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
try|try
block|{
name|fs
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
operator|new
name|Path
argument_list|(
name|dataDir
argument_list|)
operator|.
name|toUri
argument_list|()
argument_list|,
name|getConf
argument_list|()
argument_list|)
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
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|debug
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"UpdateHandler init: tlogDir="
operator|+
name|tlogDir
operator|+
literal|", next id="
operator|+
name|id
argument_list|,
literal|" this is a reopen or double init ... nothing else to do."
argument_list|)
expr_stmt|;
block|}
name|versionInfo
operator|.
name|reload
argument_list|()
expr_stmt|;
return|return;
block|}
block|}
name|tlogDir
operator|=
operator|new
name|Path
argument_list|(
name|dataDir
argument_list|,
name|TLOG_NAME
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|fs
operator|.
name|exists
argument_list|(
name|tlogDir
argument_list|)
condition|)
block|{
name|boolean
name|success
init|=
name|fs
operator|.
name|mkdirs
argument_list|(
name|tlogDir
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|success
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Could not create directory:"
operator|+
name|tlogDir
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|fs
operator|.
name|mkdirs
argument_list|(
name|tlogDir
argument_list|)
expr_stmt|;
comment|// To check for safe mode
block|}
break|break;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getClassName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"org.apache.hadoop.hdfs.server.namenode.SafeModeException"
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"The NameNode is in SafeMode - Solr will wait 5 seconds and try again."
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e1
parameter_list|)
block|{
name|Thread
operator|.
name|interrupted
argument_list|()
expr_stmt|;
block|}
continue|continue;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Problem creating directory: "
operator|+
name|tlogDir
argument_list|,
name|e
argument_list|)
throw|;
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
literal|"Problem creating directory: "
operator|+
name|tlogDir
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|tlogFiles
operator|=
name|getLogList
argument_list|(
name|fs
argument_list|,
name|tlogDir
argument_list|)
expr_stmt|;
name|id
operator|=
name|getLastLogId
argument_list|()
operator|+
literal|1
expr_stmt|;
comment|// add 1 since we will create a new log for the
comment|// next update
if|if
condition|(
name|debug
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"UpdateHandler init: tlogDir="
operator|+
name|tlogDir
operator|+
literal|", existing tlogs="
operator|+
name|Arrays
operator|.
name|asList
argument_list|(
name|tlogFiles
argument_list|)
operator|+
literal|", next id="
operator|+
name|id
argument_list|)
expr_stmt|;
block|}
name|TransactionLog
name|oldLog
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|oldLogName
range|:
name|tlogFiles
control|)
block|{
name|Path
name|f
init|=
operator|new
name|Path
argument_list|(
name|tlogDir
argument_list|,
name|oldLogName
argument_list|)
decl_stmt|;
try|try
block|{
name|oldLog
operator|=
operator|new
name|HdfsTransactionLog
argument_list|(
name|fs
argument_list|,
name|f
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
name|tlogDfsReplication
argument_list|)
expr_stmt|;
name|addOldLog
argument_list|(
name|oldLog
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// don't remove old logs on startup since more
comment|// than one may be uncapped.
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|INIT_FAILED_LOGS_COUNT
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Failure to open existing log file (non fatal) "
operator|+
name|f
argument_list|,
name|e
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|.
name|delete
argument_list|(
name|f
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e1
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e1
argument_list|)
throw|;
block|}
block|}
block|}
comment|// Record first two logs (oldest first) at startup for potential tlog
comment|// recovery.
comment|// It's possible that at abnormal close both "tlog" and "prevTlog" were
comment|// uncapped.
for|for
control|(
name|TransactionLog
name|ll
range|:
name|logs
control|)
block|{
name|newestLogsOnStartup
operator|.
name|addFirst
argument_list|(
name|ll
argument_list|)
expr_stmt|;
if|if
condition|(
name|newestLogsOnStartup
operator|.
name|size
argument_list|()
operator|>=
literal|2
condition|)
break|break;
block|}
try|try
block|{
name|versionInfo
operator|=
operator|new
name|VersionInfo
argument_list|(
name|this
argument_list|,
name|numVersionBuckets
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Unable to use updateLog: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Unable to use updateLog: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
comment|// TODO: these startingVersions assume that we successfully recover from all
comment|// non-complete tlogs.
try|try
init|(
name|RecentUpdates
name|startingUpdates
init|=
name|getRecentUpdates
argument_list|()
init|)
block|{
name|startingVersions
operator|=
name|startingUpdates
operator|.
name|getVersions
argument_list|(
name|getNumRecordsToKeep
argument_list|()
argument_list|)
expr_stmt|;
name|startingOperation
operator|=
name|startingUpdates
operator|.
name|getLatestOperation
argument_list|()
expr_stmt|;
comment|// populate recent deletes list (since we can't get that info from the
comment|// index)
for|for
control|(
name|int
name|i
init|=
name|startingUpdates
operator|.
name|deleteList
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|DeleteUpdate
name|du
init|=
name|startingUpdates
operator|.
name|deleteList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|oldDeletes
operator|.
name|put
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|du
operator|.
name|id
argument_list|)
argument_list|,
operator|new
name|LogPtr
argument_list|(
operator|-
literal|1
argument_list|,
name|du
operator|.
name|version
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// populate recent deleteByQuery commands
for|for
control|(
name|int
name|i
init|=
name|startingUpdates
operator|.
name|deleteByQueryList
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|Update
name|update
init|=
name|startingUpdates
operator|.
name|deleteByQueryList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|dbq
init|=
operator|(
name|List
argument_list|<
name|Object
argument_list|>
operator|)
name|update
operator|.
name|log
operator|.
name|lookup
argument_list|(
name|update
operator|.
name|pointer
argument_list|)
decl_stmt|;
name|long
name|version
init|=
operator|(
name|Long
operator|)
name|dbq
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|String
name|q
init|=
operator|(
name|String
operator|)
name|dbq
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|trackDeleteByQuery
argument_list|(
name|q
argument_list|,
name|version
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getLogDir
specifier|public
name|String
name|getLogDir
parameter_list|()
block|{
return|return
name|tlogDir
operator|.
name|toUri
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getLogList
specifier|public
specifier|static
name|String
index|[]
name|getLogList
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|tlogDir
parameter_list|)
block|{
specifier|final
name|String
name|prefix
init|=
name|TLOG_NAME
operator|+
literal|'.'
decl_stmt|;
assert|assert
name|fs
operator|!=
literal|null
assert|;
name|FileStatus
index|[]
name|fileStatuses
decl_stmt|;
try|try
block|{
name|fileStatuses
operator|=
name|fs
operator|.
name|listStatus
argument_list|(
name|tlogDir
argument_list|,
operator|new
name|PathFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
return|return
name|path
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
return|;
block|}
block|}
argument_list|)
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
name|String
index|[]
name|names
init|=
operator|new
name|String
index|[
name|fileStatuses
operator|.
name|length
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
name|fileStatuses
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|names
index|[
name|i
index|]
operator|=
name|fileStatuses
index|[
name|i
index|]
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|names
argument_list|)
expr_stmt|;
return|return
name|names
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|(
name|boolean
name|committed
parameter_list|)
block|{
name|close
argument_list|(
name|committed
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|(
name|boolean
name|committed
parameter_list|,
name|boolean
name|deleteOnClose
parameter_list|)
block|{
try|try
block|{
name|super
operator|.
name|close
argument_list|(
name|committed
argument_list|,
name|deleteOnClose
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|fs
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|ensureLog
specifier|protected
name|void
name|ensureLog
parameter_list|()
block|{
if|if
condition|(
name|tlog
operator|==
literal|null
condition|)
block|{
name|String
name|newLogName
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
name|LOG_FILENAME_PATTERN
argument_list|,
name|TLOG_NAME
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|HdfsTransactionLog
name|ntlog
init|=
operator|new
name|HdfsTransactionLog
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
name|tlogDir
argument_list|,
name|newLogName
argument_list|)
argument_list|,
name|globalStrings
argument_list|,
name|tlogDfsReplication
argument_list|)
decl_stmt|;
name|tlog
operator|=
name|ntlog
expr_stmt|;
if|if
condition|(
name|tlog
operator|!=
name|ntlog
condition|)
block|{
name|ntlog
operator|.
name|deleteOnClose
operator|=
literal|false
expr_stmt|;
name|ntlog
operator|.
name|decref
argument_list|()
expr_stmt|;
name|ntlog
operator|.
name|forceClose
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Clears the logs on the file system. Only call before init.    *     * @param core the SolrCore    * @param ulogPluginInfo the init info for the UpdateHandler    */
annotation|@
name|Override
DECL|method|clearLog
specifier|public
name|void
name|clearLog
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|PluginInfo
name|ulogPluginInfo
parameter_list|)
block|{
if|if
condition|(
name|ulogPluginInfo
operator|==
literal|null
condition|)
return|return;
name|Path
name|tlogDir
init|=
operator|new
name|Path
argument_list|(
name|getTlogDir
argument_list|(
name|core
argument_list|,
name|ulogPluginInfo
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|fs
operator|!=
literal|null
operator|&&
name|fs
operator|.
name|exists
argument_list|(
name|tlogDir
argument_list|)
condition|)
block|{
name|String
index|[]
name|files
init|=
name|getLogList
argument_list|(
name|tlogDir
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
name|Path
name|f
init|=
operator|new
name|Path
argument_list|(
name|tlogDir
argument_list|,
name|file
argument_list|)
decl_stmt|;
name|boolean
name|s
init|=
name|fs
operator|.
name|delete
argument_list|(
name|f
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|s
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Could not remove tlog file:"
operator|+
name|f
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
annotation|@
name|Override
DECL|method|preSoftCommit
specifier|public
name|void
name|preSoftCommit
parameter_list|(
name|CommitUpdateCommand
name|cmd
parameter_list|)
block|{
name|debug
operator|=
name|log
operator|.
name|isDebugEnabled
argument_list|()
expr_stmt|;
name|super
operator|.
name|preSoftCommit
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
DECL|method|getLogList
specifier|public
name|String
index|[]
name|getLogList
parameter_list|(
name|Path
name|tlogDir
parameter_list|)
throws|throws
name|FileNotFoundException
throws|,
name|IOException
block|{
specifier|final
name|String
name|prefix
init|=
name|TLOG_NAME
operator|+
literal|'.'
decl_stmt|;
name|FileStatus
index|[]
name|files
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|tlogDir
argument_list|,
operator|new
name|PathFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|Path
name|name
parameter_list|)
block|{
return|return
name|name
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fileList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|files
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|FileStatus
name|file
range|:
name|files
control|)
block|{
name|fileList
operator|.
name|add
argument_list|(
name|file
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|fileList
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
return|;
block|}
comment|/**    * Returns true if we were able to drop buffered updates and return to the    * ACTIVE state    */
comment|// public boolean dropBufferedUpdates() {
comment|// versionInfo.blockUpdates();
comment|// try {
comment|// if (state != State.BUFFERING) return false;
comment|//
comment|// if (log.isInfoEnabled()) {
comment|// log.info("Dropping buffered updates " + this);
comment|// }
comment|//
comment|// // since we blocked updates, this synchronization shouldn't strictly be
comment|// necessary.
comment|// synchronized (this) {
comment|// if (tlog != null) {
comment|// tlog.rollback(recoveryInfo.positionOfStart);
comment|// }
comment|// }
comment|//
comment|// state = State.ACTIVE;
comment|// operationFlags&= ~FLAG_GAP;
comment|// } catch (IOException e) {
comment|// SolrException.log(log,"Error attempting to roll back log", e);
comment|// return false;
comment|// }
comment|// finally {
comment|// versionInfo.unblockUpdates();
comment|// }
comment|// return true;
comment|// }
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"HDFSUpdateLog{state="
operator|+
name|getState
argument_list|()
operator|+
literal|", tlog="
operator|+
name|tlog
operator|+
literal|"}"
return|;
block|}
block|}
end_class
end_unit
