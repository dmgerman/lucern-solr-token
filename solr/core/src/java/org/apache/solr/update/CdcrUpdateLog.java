begin_unit
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
name|Map
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
name|ConcurrentHashMap
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
name|LinkedBlockingDeque
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
comment|/**  * An extension of the {@link org.apache.solr.update.UpdateLog} for the CDCR scenario.<br>  * Compared to the original update log implementation, transaction logs are removed based on  * pointers instead of a fixed size limit. Pointers are created by the CDC replicators and  * correspond to replication checkpoints. If all pointers are ahead of a transaction log,  * this transaction log is removed.<br>  * Given that the number of transaction logs can become considerable if some pointers are  * lagging behind, the {@link org.apache.solr.update.CdcrUpdateLog.CdcrLogReader} provides  * a {@link org.apache.solr.update.CdcrUpdateLog.CdcrLogReader#seek(long)} method to  * efficiently lookup a particular transaction log file given a version number.  */
end_comment
begin_class
DECL|class|CdcrUpdateLog
specifier|public
class|class
name|CdcrUpdateLog
extends|extends
name|UpdateLog
block|{
DECL|field|logPointers
specifier|protected
specifier|final
name|Map
argument_list|<
name|CdcrLogReader
argument_list|,
name|CdcrLogPointer
argument_list|>
name|logPointers
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * A reader that will be used as toggle to turn on/off the buffering of tlogs    */
DECL|field|bufferToggle
specifier|private
name|CdcrLogReader
name|bufferToggle
decl_stmt|;
DECL|field|LOG_FILENAME_PATTERN
specifier|public
specifier|static
name|String
name|LOG_FILENAME_PATTERN
init|=
literal|"%s.%019d.%1d"
decl_stmt|;
DECL|field|log
specifier|protected
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CdcrUpdateLog
operator|.
name|class
argument_list|)
decl_stmt|;
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
comment|// remove dangling readers
for|for
control|(
name|CdcrLogReader
name|reader
range|:
name|logPointers
operator|.
name|keySet
argument_list|()
control|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|logPointers
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// init
name|super
operator|.
name|init
argument_list|(
name|uhandler
argument_list|,
name|core
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newTransactionLog
specifier|public
name|TransactionLog
name|newTransactionLog
parameter_list|(
name|File
name|tlogFile
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|globalStrings
parameter_list|,
name|boolean
name|openExisting
parameter_list|)
block|{
return|return
operator|new
name|CdcrTransactionLog
argument_list|(
name|tlogFile
argument_list|,
name|globalStrings
argument_list|,
name|openExisting
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|addOldLog
specifier|protected
name|void
name|addOldLog
parameter_list|(
name|TransactionLog
name|oldLog
parameter_list|,
name|boolean
name|removeOld
parameter_list|)
block|{
if|if
condition|(
name|oldLog
operator|==
literal|null
condition|)
return|return;
name|numOldRecords
operator|+=
name|oldLog
operator|.
name|numRecords
argument_list|()
expr_stmt|;
name|int
name|currRecords
init|=
name|numOldRecords
decl_stmt|;
if|if
condition|(
name|oldLog
operator|!=
name|tlog
operator|&&
name|tlog
operator|!=
literal|null
condition|)
block|{
name|currRecords
operator|+=
name|tlog
operator|.
name|numRecords
argument_list|()
expr_stmt|;
block|}
while|while
condition|(
name|removeOld
operator|&&
name|logs
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|TransactionLog
name|log
init|=
name|logs
operator|.
name|peekLast
argument_list|()
decl_stmt|;
name|int
name|nrec
init|=
name|log
operator|.
name|numRecords
argument_list|()
decl_stmt|;
comment|// remove oldest log if we don't need it to keep at least numRecordsToKeep, or if
comment|// we already have the limit of 10 log files.
if|if
condition|(
name|currRecords
operator|-
name|nrec
operator|>=
name|numRecordsToKeep
operator|||
name|logs
operator|.
name|size
argument_list|()
operator|>=
literal|10
condition|)
block|{
comment|// remove the oldest log if nobody points to it
if|if
condition|(
operator|!
name|this
operator|.
name|hasLogPointer
argument_list|(
name|log
argument_list|)
condition|)
block|{
name|currRecords
operator|-=
name|nrec
expr_stmt|;
name|numOldRecords
operator|-=
name|nrec
expr_stmt|;
name|TransactionLog
name|last
init|=
name|logs
operator|.
name|removeLast
argument_list|()
decl_stmt|;
name|last
operator|.
name|deleteOnClose
operator|=
literal|true
expr_stmt|;
name|last
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// it will be deleted if no longer in use
continue|continue;
block|}
comment|// we have one log with one pointer, we should stop removing logs
break|break;
block|}
break|break;
block|}
comment|// Decref old log as we do not write to it anymore
comment|// If the oldlog is uncapped, i.e., a write commit has to be performed
comment|// during recovery, the output stream will be automatically re-open when
comment|// TransaactionLog#incref will be called.
name|oldLog
operator|.
name|deleteOnClose
operator|=
literal|false
expr_stmt|;
name|oldLog
operator|.
name|decref
argument_list|()
expr_stmt|;
comment|// don't incref... we are taking ownership from the caller.
name|logs
operator|.
name|addFirst
argument_list|(
name|oldLog
argument_list|)
expr_stmt|;
block|}
comment|/**    * Checks if one of the log pointer is pointing to the given tlog.    */
DECL|method|hasLogPointer
specifier|private
name|boolean
name|hasLogPointer
parameter_list|(
name|TransactionLog
name|tlog
parameter_list|)
block|{
for|for
control|(
name|CdcrLogPointer
name|pointer
range|:
name|logPointers
operator|.
name|values
argument_list|()
control|)
block|{
comment|// if we have a pointer that is not initialised, then do not remove the old tlogs
comment|// as we have a log reader that didn't pick them up yet.
if|if
condition|(
operator|!
name|pointer
operator|.
name|isInitialised
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|pointer
operator|.
name|tlogFile
operator|==
name|tlog
operator|.
name|tlogFile
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getLastLogId
specifier|public
name|long
name|getLastLogId
parameter_list|()
block|{
if|if
condition|(
name|id
operator|!=
operator|-
literal|1
condition|)
return|return
name|id
return|;
if|if
condition|(
name|tlogFiles
operator|.
name|length
operator|==
literal|0
condition|)
return|return
operator|-
literal|1
return|;
name|String
name|last
init|=
name|tlogFiles
index|[
name|tlogFiles
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|last
operator|.
name|substring
argument_list|(
name|TLOG_NAME
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|,
name|last
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|,
name|boolean
name|clearCaches
parameter_list|)
block|{
comment|// Ensure we create a new tlog file following our filename format,
comment|// the variable tlog will be not null, and the ensureLog of the parent will be skipped
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
operator|(
name|cmd
operator|.
name|getFlags
argument_list|()
operator|&
name|UpdateCommand
operator|.
name|REPLAY
operator|)
operator|==
literal|0
condition|)
block|{
name|ensureLog
argument_list|(
name|cmd
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Then delegate to parent method
name|super
operator|.
name|add
argument_list|(
name|cmd
argument_list|,
name|clearCaches
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|delete
specifier|public
name|void
name|delete
parameter_list|(
name|DeleteUpdateCommand
name|cmd
parameter_list|)
block|{
comment|// Ensure we create a new tlog file following our filename format
comment|// the variable tlog will be not null, and the ensureLog of the parent will be skipped
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
operator|(
name|cmd
operator|.
name|getFlags
argument_list|()
operator|&
name|UpdateCommand
operator|.
name|REPLAY
operator|)
operator|==
literal|0
condition|)
block|{
name|ensureLog
argument_list|(
name|cmd
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Then delegate to parent method
name|super
operator|.
name|delete
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|deleteByQuery
specifier|public
name|void
name|deleteByQuery
parameter_list|(
name|DeleteUpdateCommand
name|cmd
parameter_list|)
block|{
comment|// Ensure we create a new tlog file following our filename format
comment|// the variable tlog will be not null, and the ensureLog of the parent will be skipped
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
operator|(
name|cmd
operator|.
name|getFlags
argument_list|()
operator|&
name|UpdateCommand
operator|.
name|REPLAY
operator|)
operator|==
literal|0
condition|)
block|{
name|ensureLog
argument_list|(
name|cmd
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Then delegate to parent method
name|super
operator|.
name|deleteByQuery
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new {@link org.apache.solr.update.CdcrUpdateLog.CdcrLogReader}    * initialised with the current list of tlogs.    */
DECL|method|newLogReader
specifier|public
name|CdcrLogReader
name|newLogReader
parameter_list|()
block|{
return|return
operator|new
name|CdcrLogReader
argument_list|(
operator|new
name|ArrayList
argument_list|(
name|logs
argument_list|)
argument_list|,
name|tlog
argument_list|)
return|;
block|}
comment|/**    * Enable the buffering of the tlogs. When buffering is activated, the update logs will not remove any    * old transaction log files.    */
DECL|method|enableBuffer
specifier|public
name|void
name|enableBuffer
parameter_list|()
block|{
if|if
condition|(
name|bufferToggle
operator|==
literal|null
condition|)
block|{
name|bufferToggle
operator|=
name|this
operator|.
name|newLogReader
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Disable the buffering of the tlogs.    */
DECL|method|disableBuffer
specifier|public
name|void
name|disableBuffer
parameter_list|()
block|{
if|if
condition|(
name|bufferToggle
operator|!=
literal|null
condition|)
block|{
name|bufferToggle
operator|.
name|close
argument_list|()
expr_stmt|;
name|bufferToggle
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|getBufferToggle
specifier|public
name|CdcrLogReader
name|getBufferToggle
parameter_list|()
block|{
return|return
name|bufferToggle
return|;
block|}
comment|/**    * Is the update log buffering the tlogs ?    */
DECL|method|isBuffering
specifier|public
name|boolean
name|isBuffering
parameter_list|()
block|{
return|return
name|bufferToggle
operator|==
literal|null
condition|?
literal|false
else|:
literal|true
return|;
block|}
DECL|method|ensureLog
specifier|protected
name|void
name|ensureLog
parameter_list|(
name|long
name|startVersion
parameter_list|)
block|{
if|if
condition|(
name|tlog
operator|==
literal|null
condition|)
block|{
name|long
name|absoluteVersion
init|=
name|Math
operator|.
name|abs
argument_list|(
name|startVersion
argument_list|)
decl_stmt|;
comment|// version is negative for deletes
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
argument_list|,
name|absoluteVersion
argument_list|)
decl_stmt|;
name|tlog
operator|=
operator|new
name|CdcrTransactionLog
argument_list|(
operator|new
name|File
argument_list|(
name|tlogDir
argument_list|,
name|newLogName
argument_list|)
argument_list|,
name|globalStrings
argument_list|)
expr_stmt|;
block|}
comment|// push the new tlog to the opened readers
for|for
control|(
name|CdcrLogReader
name|reader
range|:
name|logPointers
operator|.
name|keySet
argument_list|()
control|)
block|{
name|reader
operator|.
name|push
argument_list|(
name|tlog
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * expert: Reset the update log before initialisation. This is needed by the IndexFetcher during a    * a Recovery operation in order to re-initialise the UpdateLog with a new set of tlog files.    */
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
comment|// Close readers
for|for
control|(
name|CdcrLogReader
name|reader
range|:
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|logPointers
operator|.
name|keySet
argument_list|()
argument_list|)
control|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|logPointers
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// Close and clear logs
name|doClose
argument_list|(
name|prevTlog
argument_list|)
expr_stmt|;
name|doClose
argument_list|(
name|tlog
argument_list|)
expr_stmt|;
for|for
control|(
name|TransactionLog
name|log
range|:
name|logs
control|)
block|{
if|if
condition|(
name|log
operator|==
name|prevTlog
operator|||
name|log
operator|==
name|tlog
condition|)
continue|continue;
name|doClose
argument_list|(
name|log
argument_list|)
expr_stmt|;
block|}
name|logs
operator|.
name|clear
argument_list|()
expr_stmt|;
name|newestLogsOnStartup
operator|.
name|clear
argument_list|()
expr_stmt|;
name|tlog
operator|=
name|prevTlog
operator|=
literal|null
expr_stmt|;
name|prevMapLog
operator|=
name|prevMapLog2
operator|=
literal|null
expr_stmt|;
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|prevMap
operator|!=
literal|null
condition|)
name|prevMap
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|prevMap2
operator|!=
literal|null
condition|)
name|prevMap2
operator|.
name|clear
argument_list|()
expr_stmt|;
name|numOldRecords
operator|=
literal|0
expr_stmt|;
name|oldDeletes
operator|.
name|clear
argument_list|()
expr_stmt|;
name|deleteByQueries
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// reset lastDataDir for triggering full #init()
name|lastDataDir
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|doClose
specifier|private
name|void
name|doClose
parameter_list|(
name|TransactionLog
name|theLog
parameter_list|)
block|{
if|if
condition|(
name|theLog
operator|!=
literal|null
condition|)
block|{
name|theLog
operator|.
name|deleteOnClose
operator|=
literal|false
expr_stmt|;
name|theLog
operator|.
name|decref
argument_list|()
expr_stmt|;
name|theLog
operator|.
name|forceClose
argument_list|()
expr_stmt|;
block|}
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
for|for
control|(
name|CdcrLogReader
name|reader
range|:
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|logPointers
operator|.
name|keySet
argument_list|()
argument_list|)
control|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
DECL|class|CdcrLogPointer
specifier|private
specifier|static
class|class
name|CdcrLogPointer
block|{
DECL|field|tlogFile
name|File
name|tlogFile
init|=
literal|null
decl_stmt|;
DECL|method|CdcrLogPointer
specifier|private
name|CdcrLogPointer
parameter_list|()
block|{     }
DECL|method|set
specifier|private
name|void
name|set
parameter_list|(
name|File
name|tlogFile
parameter_list|)
block|{
name|this
operator|.
name|tlogFile
operator|=
name|tlogFile
expr_stmt|;
block|}
DECL|method|isInitialised
specifier|private
name|boolean
name|isInitialised
parameter_list|()
block|{
return|return
name|tlogFile
operator|==
literal|null
condition|?
literal|false
else|:
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"CdcrLogPointer("
operator|+
name|tlogFile
operator|+
literal|")"
return|;
block|}
block|}
DECL|class|CdcrLogReader
specifier|public
class|class
name|CdcrLogReader
block|{
DECL|field|currentTlog
specifier|private
name|TransactionLog
name|currentTlog
decl_stmt|;
DECL|field|tlogReader
specifier|private
name|TransactionLog
operator|.
name|LogReader
name|tlogReader
decl_stmt|;
comment|// we need to use a blocking deque because of #getNumberOfRemainingRecords
DECL|field|tlogs
specifier|private
specifier|final
name|LinkedBlockingDeque
argument_list|<
name|TransactionLog
argument_list|>
name|tlogs
decl_stmt|;
DECL|field|pointer
specifier|private
specifier|final
name|CdcrLogPointer
name|pointer
decl_stmt|;
comment|/**      * Used to record the last position of the tlog      */
DECL|field|lastPositionInTLog
specifier|private
name|long
name|lastPositionInTLog
init|=
literal|0
decl_stmt|;
comment|/**      * lastVersion is used to get nextToLastVersion      */
DECL|field|lastVersion
specifier|private
name|long
name|lastVersion
init|=
operator|-
literal|1
decl_stmt|;
comment|/**      * nextToLastVersion is communicated by leader to replicas so that they can remove no longer needed tlogs      *<p>      * nextToLastVersion is used because thanks to {@link #resetToLastPosition()} lastVersion can become the current version      */
DECL|field|nextToLastVersion
specifier|private
name|long
name|nextToLastVersion
init|=
operator|-
literal|1
decl_stmt|;
comment|/**      * Used to record the number of records read in the current tlog      */
DECL|field|numRecordsReadInCurrentTlog
specifier|private
name|long
name|numRecordsReadInCurrentTlog
init|=
literal|0
decl_stmt|;
DECL|method|CdcrLogReader
specifier|private
name|CdcrLogReader
parameter_list|(
name|List
argument_list|<
name|TransactionLog
argument_list|>
name|tlogs
parameter_list|,
name|TransactionLog
name|tlog
parameter_list|)
block|{
name|this
operator|.
name|tlogs
operator|=
operator|new
name|LinkedBlockingDeque
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|tlogs
operator|.
name|addAll
argument_list|(
name|tlogs
argument_list|)
expr_stmt|;
if|if
condition|(
name|tlog
operator|!=
literal|null
condition|)
name|this
operator|.
name|tlogs
operator|.
name|push
argument_list|(
name|tlog
argument_list|)
expr_stmt|;
comment|// ensure that the tlog being written is pushed
comment|// Register the pointer in the parent UpdateLog
name|pointer
operator|=
operator|new
name|CdcrLogPointer
argument_list|()
expr_stmt|;
name|logPointers
operator|.
name|put
argument_list|(
name|this
argument_list|,
name|pointer
argument_list|)
expr_stmt|;
comment|// If the reader is initialised while the updates log is empty, do nothing
if|if
condition|(
operator|(
name|currentTlog
operator|=
name|this
operator|.
name|tlogs
operator|.
name|peekLast
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|tlogReader
operator|=
name|currentTlog
operator|.
name|getReader
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|pointer
operator|.
name|set
argument_list|(
name|currentTlog
operator|.
name|tlogFile
argument_list|)
expr_stmt|;
name|numRecordsReadInCurrentTlog
operator|=
literal|0
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Init new tlog reader for {} - tlogReader = {}"
argument_list|,
name|currentTlog
operator|.
name|tlogFile
argument_list|,
name|tlogReader
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|push
specifier|private
name|void
name|push
parameter_list|(
name|TransactionLog
name|tlog
parameter_list|)
block|{
name|this
operator|.
name|tlogs
operator|.
name|push
argument_list|(
name|tlog
argument_list|)
expr_stmt|;
comment|// The reader was initialised while the update logs was empty, or reader was exhausted previously,
comment|// we have to update the current tlog and the associated tlog reader.
if|if
condition|(
name|currentTlog
operator|==
literal|null
operator|&&
operator|!
name|tlogs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|currentTlog
operator|=
name|tlogs
operator|.
name|peekLast
argument_list|()
expr_stmt|;
name|tlogReader
operator|=
name|currentTlog
operator|.
name|getReader
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|pointer
operator|.
name|set
argument_list|(
name|currentTlog
operator|.
name|tlogFile
argument_list|)
expr_stmt|;
name|numRecordsReadInCurrentTlog
operator|=
literal|0
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Init new tlog reader for {} - tlogReader = {}"
argument_list|,
name|currentTlog
operator|.
name|tlogFile
argument_list|,
name|tlogReader
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Expert: Instantiate a sub-reader. A sub-reader is used for batch updates. It allows to iterates over the      * update logs entries without modifying the state of the parent log reader. If the batch update fails, the state      * of the sub-reader is discarded and the state of the parent reader is not modified. If the batch update      * is successful, the sub-reader is used to fast forward the parent reader with the method      * {@link #forwardSeek(org.apache.solr.update.CdcrUpdateLog.CdcrLogReader)}.      */
DECL|method|getSubReader
specifier|public
name|CdcrLogReader
name|getSubReader
parameter_list|()
block|{
comment|// Add the last element of the queue to properly initialise the pointer and log reader
name|CdcrLogReader
name|clone
init|=
operator|new
name|CdcrLogReader
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|TransactionLog
argument_list|>
argument_list|()
argument_list|,
name|this
operator|.
name|tlogs
operator|.
name|peekLast
argument_list|()
argument_list|)
decl_stmt|;
name|clone
operator|.
name|tlogs
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// clear queue before copy
name|clone
operator|.
name|tlogs
operator|.
name|addAll
argument_list|(
name|tlogs
argument_list|)
expr_stmt|;
comment|// perform a copy of the list
name|clone
operator|.
name|lastPositionInTLog
operator|=
name|this
operator|.
name|lastPositionInTLog
expr_stmt|;
name|clone
operator|.
name|numRecordsReadInCurrentTlog
operator|=
name|this
operator|.
name|numRecordsReadInCurrentTlog
expr_stmt|;
name|clone
operator|.
name|lastVersion
operator|=
name|this
operator|.
name|lastVersion
expr_stmt|;
name|clone
operator|.
name|nextToLastVersion
operator|=
name|this
operator|.
name|nextToLastVersion
expr_stmt|;
comment|// If the update log is not empty, we need to initialise the tlog reader
comment|// NB: the tlogReader is equal to null if the update log is empty
if|if
condition|(
name|tlogReader
operator|!=
literal|null
condition|)
block|{
name|clone
operator|.
name|tlogReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|clone
operator|.
name|tlogReader
operator|=
name|currentTlog
operator|.
name|getReader
argument_list|(
name|this
operator|.
name|tlogReader
operator|.
name|currentPos
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|clone
return|;
block|}
comment|/**      * Expert: Fast forward this log reader with a log subreader. The subreader will be closed after calling this      * method. In order to avoid unexpected results, the log      * subreader must be created from this reader with the method {@link #getSubReader()}.      */
DECL|method|forwardSeek
specifier|public
name|void
name|forwardSeek
parameter_list|(
name|CdcrLogReader
name|subReader
parameter_list|)
block|{
comment|// If a subreader has a null tlog reader, does nothing
comment|// This can happend if a subreader is instantiated from a non-initialised parent reader, or if the subreader
comment|// has been closed.
if|if
condition|(
name|subReader
operator|.
name|tlogReader
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|tlogReader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// close the existing reader, a new one will be created
while|while
condition|(
name|this
operator|.
name|tlogs
operator|.
name|peekLast
argument_list|()
operator|.
name|id
operator|<
name|subReader
operator|.
name|tlogs
operator|.
name|peekLast
argument_list|()
operator|.
name|id
condition|)
block|{
name|tlogs
operator|.
name|removeLast
argument_list|()
expr_stmt|;
name|currentTlog
operator|=
name|tlogs
operator|.
name|peekLast
argument_list|()
expr_stmt|;
block|}
assert|assert
name|this
operator|.
name|tlogs
operator|.
name|peekLast
argument_list|()
operator|.
name|id
operator|==
name|subReader
operator|.
name|tlogs
operator|.
name|peekLast
argument_list|()
operator|.
name|id
assert|;
name|this
operator|.
name|pointer
operator|.
name|set
argument_list|(
name|currentTlog
operator|.
name|tlogFile
argument_list|)
expr_stmt|;
name|this
operator|.
name|lastPositionInTLog
operator|=
name|subReader
operator|.
name|lastPositionInTLog
expr_stmt|;
name|this
operator|.
name|numRecordsReadInCurrentTlog
operator|=
name|subReader
operator|.
name|numRecordsReadInCurrentTlog
expr_stmt|;
name|this
operator|.
name|lastVersion
operator|=
name|subReader
operator|.
name|lastVersion
expr_stmt|;
name|this
operator|.
name|nextToLastVersion
operator|=
name|subReader
operator|.
name|nextToLastVersion
expr_stmt|;
name|this
operator|.
name|tlogReader
operator|=
name|currentTlog
operator|.
name|getReader
argument_list|(
name|subReader
operator|.
name|tlogReader
operator|.
name|currentPos
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Advances to the next log entry in the updates log and returns the log entry itself.      * Returns null if there are no more log entries in the updates log.<br>      *<p>      *<b>NOTE:</b> after the reader has exhausted, you can call again this method since the updates      * log might have been updated with new entries.      */
DECL|method|next
specifier|public
name|Object
name|next
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
while|while
condition|(
operator|!
name|tlogs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|lastPositionInTLog
operator|=
name|tlogReader
operator|.
name|currentPos
argument_list|()
expr_stmt|;
name|Object
name|o
init|=
name|tlogReader
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
block|{
name|pointer
operator|.
name|set
argument_list|(
name|currentTlog
operator|.
name|tlogFile
argument_list|)
expr_stmt|;
name|nextToLastVersion
operator|=
name|lastVersion
expr_stmt|;
name|lastVersion
operator|=
name|getVersion
argument_list|(
name|o
argument_list|)
expr_stmt|;
name|numRecordsReadInCurrentTlog
operator|++
expr_stmt|;
return|return
name|o
return|;
block|}
if|if
condition|(
name|tlogs
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
comment|// if the current tlog is not the newest one, we can advance to the next one
name|tlogReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|tlogs
operator|.
name|removeLast
argument_list|()
expr_stmt|;
name|currentTlog
operator|=
name|tlogs
operator|.
name|peekLast
argument_list|()
expr_stmt|;
name|tlogReader
operator|=
name|currentTlog
operator|.
name|getReader
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|pointer
operator|.
name|set
argument_list|(
name|currentTlog
operator|.
name|tlogFile
argument_list|)
expr_stmt|;
name|numRecordsReadInCurrentTlog
operator|=
literal|0
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Init new tlog reader for {} - tlogReader = {}"
argument_list|,
name|currentTlog
operator|.
name|tlogFile
argument_list|,
name|tlogReader
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// the only tlog left is the new tlog which is currently being written,
comment|// we should not remove it as we have to try to read it again later.
return|return
literal|null
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Advances to the first beyond the current whose version number is greater      * than or equal to<i>targetVersion</i>.<br>      * Returns true if the reader has been advanced. If<i>targetVersion</i> is      * greater than the highest version number in the updates log, the reader      * has been advanced to the end of the current tlog, and a call to      * {@link #next()} will probably return null.<br>      * Returns false if<i>targetVersion</i> is lower than the oldest known entry.      * In this scenario, it probably means that there is a gap in the updates log.<br>      *<p>      *<b>NOTE:</b> This method must be called before the first call to {@link #next()}.      */
DECL|method|seek
specifier|public
name|boolean
name|seek
parameter_list|(
name|long
name|targetVersion
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|Object
name|o
decl_stmt|;
comment|// version is negative for deletes - ensure that we are manipulating absolute version numbers.
name|targetVersion
operator|=
name|Math
operator|.
name|abs
argument_list|(
name|targetVersion
argument_list|)
expr_stmt|;
if|if
condition|(
name|tlogs
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|this
operator|.
name|seekTLog
argument_list|(
name|targetVersion
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// now that we might be on the right tlog, iterates over the entries to find the one we are looking for
while|while
condition|(
operator|(
name|o
operator|=
name|this
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|getVersion
argument_list|(
name|o
argument_list|)
operator|>=
name|targetVersion
condition|)
block|{
name|this
operator|.
name|resetToLastPosition
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**      * Seeks the tlog associated to the target version by using the updates log index,      * and initialises the log reader to the start of the tlog. Returns true if it was able      * to seek the corresponding tlog, false if the<i>targetVersion</i> is lower than the      * oldest known entry (which probably indicates a gap).<br>      *<p>      *<b>NOTE:</b> This method might modify the tlog queue by removing tlogs that are older      * than the target version.      */
DECL|method|seekTLog
specifier|private
name|boolean
name|seekTLog
parameter_list|(
name|long
name|targetVersion
parameter_list|)
block|{
comment|// if the target version is lower than the oldest known entry, we have probably a gap.
if|if
condition|(
name|targetVersion
operator|<
operator|(
operator|(
name|CdcrTransactionLog
operator|)
name|tlogs
operator|.
name|peekLast
argument_list|()
operator|)
operator|.
name|startVersion
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// closes existing reader before performing seek and possibly modifying the queue;
name|tlogReader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// iterates over the queue and removes old tlogs
name|TransactionLog
name|last
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|tlogs
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|CdcrTransactionLog
operator|)
name|tlogs
operator|.
name|peekLast
argument_list|()
operator|)
operator|.
name|startVersion
operator|>=
name|targetVersion
condition|)
block|{
break|break;
block|}
name|last
operator|=
name|tlogs
operator|.
name|pollLast
argument_list|()
expr_stmt|;
block|}
comment|// the last tlog removed is the one we look for, add it back to the queue
if|if
condition|(
name|last
operator|!=
literal|null
condition|)
name|tlogs
operator|.
name|addLast
argument_list|(
name|last
argument_list|)
expr_stmt|;
name|currentTlog
operator|=
name|tlogs
operator|.
name|peekLast
argument_list|()
expr_stmt|;
name|tlogReader
operator|=
name|currentTlog
operator|.
name|getReader
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|pointer
operator|.
name|set
argument_list|(
name|currentTlog
operator|.
name|tlogFile
argument_list|)
expr_stmt|;
name|numRecordsReadInCurrentTlog
operator|=
literal|0
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**      * Extracts the version number and converts it to its absolute form.      */
DECL|method|getVersion
specifier|private
name|long
name|getVersion
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|List
name|entry
init|=
operator|(
name|List
operator|)
name|o
decl_stmt|;
comment|// version is negative for delete, ensure that we are manipulating absolute version numbers
return|return
name|Math
operator|.
name|abs
argument_list|(
operator|(
name|Long
operator|)
name|entry
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * If called after {@link #next()}, it resets the reader to its last position.      */
DECL|method|resetToLastPosition
specifier|public
name|void
name|resetToLastPosition
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|tlogReader
operator|!=
literal|null
condition|)
block|{
name|tlogReader
operator|.
name|fis
operator|.
name|seek
argument_list|(
name|lastPositionInTLog
argument_list|)
expr_stmt|;
name|numRecordsReadInCurrentTlog
operator|--
expr_stmt|;
name|lastVersion
operator|=
name|nextToLastVersion
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to seek last position in tlog"
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
literal|"Failed to seek last position in tlog"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Returns the number of remaining records (including commit but excluding header) to be read in the logs.      */
DECL|method|getNumberOfRemainingRecords
specifier|public
name|long
name|getNumberOfRemainingRecords
parameter_list|()
block|{
name|long
name|numRemainingRecords
init|=
literal|0
decl_stmt|;
synchronized|synchronized
init|(
name|tlogs
init|)
block|{
for|for
control|(
name|TransactionLog
name|tlog
range|:
name|tlogs
control|)
block|{
name|numRemainingRecords
operator|+=
name|tlog
operator|.
name|numRecords
argument_list|()
operator|-
literal|1
expr_stmt|;
comment|// minus 1 as the number of records returned by the tlog includes the header
block|}
block|}
return|return
name|numRemainingRecords
operator|-
name|numRecordsReadInCurrentTlog
return|;
block|}
comment|/**      * Closes streams and remove the associated {@link org.apache.solr.update.CdcrUpdateLog.CdcrLogPointer} from the      * parent {@link org.apache.solr.update.CdcrUpdateLog}.      */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|tlogReader
operator|!=
literal|null
condition|)
block|{
name|tlogReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|tlogReader
operator|=
literal|null
expr_stmt|;
name|currentTlog
operator|=
literal|null
expr_stmt|;
block|}
name|tlogs
operator|.
name|clear
argument_list|()
expr_stmt|;
name|logPointers
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the absolute form of the version number of the last entry read. If the current version is equal      * to 0 (because of a commit), it will return the next to last version number.      */
DECL|method|getLastVersion
specifier|public
name|long
name|getLastVersion
parameter_list|()
block|{
return|return
name|lastVersion
operator|==
literal|0
condition|?
name|nextToLastVersion
else|:
name|lastVersion
return|;
block|}
block|}
block|}
end_class
end_unit
