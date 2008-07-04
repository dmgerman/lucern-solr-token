begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|FileNotFoundException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|ArrayList
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
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
name|LockObtainFailedException
import|;
end_import
begin_comment
comment|/**  * IndexReader implementation that has access to a Directory.   * Instances that have a SegmentInfos object (i. e. segmentInfos != null)  * "own" the directory, which means that they try to acquire a write lock  * whenever index modifications are performed.  */
end_comment
begin_class
DECL|class|DirectoryIndexReader
specifier|abstract
class|class
name|DirectoryIndexReader
extends|extends
name|IndexReader
block|{
DECL|field|directory
specifier|protected
name|Directory
name|directory
decl_stmt|;
DECL|field|closeDirectory
specifier|protected
name|boolean
name|closeDirectory
decl_stmt|;
DECL|field|deletionPolicy
specifier|private
name|IndexDeletionPolicy
name|deletionPolicy
decl_stmt|;
DECL|field|segmentInfos
specifier|private
name|SegmentInfos
name|segmentInfos
decl_stmt|;
DECL|field|writeLock
specifier|private
name|Lock
name|writeLock
decl_stmt|;
DECL|field|stale
specifier|private
name|boolean
name|stale
decl_stmt|;
DECL|field|synced
specifier|private
name|HashSet
name|synced
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
comment|/** Used by commit() to record pre-commit state in case    * rollback is necessary */
DECL|field|rollbackHasChanges
specifier|private
name|boolean
name|rollbackHasChanges
decl_stmt|;
DECL|field|rollbackSegmentInfos
specifier|private
name|SegmentInfos
name|rollbackSegmentInfos
decl_stmt|;
DECL|method|init
name|void
name|init
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentInfos
name|segmentInfos
parameter_list|,
name|boolean
name|closeDirectory
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|this
operator|.
name|segmentInfos
operator|=
name|segmentInfos
expr_stmt|;
name|this
operator|.
name|closeDirectory
operator|=
name|closeDirectory
expr_stmt|;
if|if
condition|(
name|segmentInfos
operator|!=
literal|null
condition|)
block|{
comment|// We assume that this segments_N was previously
comment|// properly sync'd:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|segmentInfos
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|SegmentInfo
name|info
init|=
name|segmentInfos
operator|.
name|info
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|List
name|files
init|=
name|info
operator|.
name|files
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|files
operator|.
name|size
argument_list|()
condition|;
name|j
operator|++
control|)
name|synced
operator|.
name|add
argument_list|(
name|files
operator|.
name|get
argument_list|(
name|j
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|DirectoryIndexReader
specifier|protected
name|DirectoryIndexReader
parameter_list|()
block|{}
DECL|method|DirectoryIndexReader
name|DirectoryIndexReader
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentInfos
name|segmentInfos
parameter_list|,
name|boolean
name|closeDirectory
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|()
expr_stmt|;
name|init
argument_list|(
name|directory
argument_list|,
name|segmentInfos
argument_list|,
name|closeDirectory
argument_list|)
expr_stmt|;
block|}
DECL|method|open
specifier|static
name|DirectoryIndexReader
name|open
parameter_list|(
specifier|final
name|Directory
name|directory
parameter_list|,
specifier|final
name|boolean
name|closeDirectory
parameter_list|,
specifier|final
name|IndexDeletionPolicy
name|deletionPolicy
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
return|return
name|open
argument_list|(
name|directory
argument_list|,
name|closeDirectory
argument_list|,
name|deletionPolicy
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|open
specifier|static
name|DirectoryIndexReader
name|open
parameter_list|(
specifier|final
name|Directory
name|directory
parameter_list|,
specifier|final
name|boolean
name|closeDirectory
parameter_list|,
specifier|final
name|IndexDeletionPolicy
name|deletionPolicy
parameter_list|,
specifier|final
name|IndexCommit
name|commit
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|SegmentInfos
operator|.
name|FindSegmentsFile
name|finder
init|=
operator|new
name|SegmentInfos
operator|.
name|FindSegmentsFile
argument_list|(
name|directory
argument_list|)
block|{
specifier|protected
name|Object
name|doBody
parameter_list|(
name|String
name|segmentFileName
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|SegmentInfos
name|infos
init|=
operator|new
name|SegmentInfos
argument_list|()
decl_stmt|;
name|infos
operator|.
name|read
argument_list|(
name|directory
argument_list|,
name|segmentFileName
argument_list|)
expr_stmt|;
name|DirectoryIndexReader
name|reader
decl_stmt|;
if|if
condition|(
name|infos
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// index is optimized
name|reader
operator|=
name|SegmentReader
operator|.
name|get
argument_list|(
name|infos
argument_list|,
name|infos
operator|.
name|info
argument_list|(
literal|0
argument_list|)
argument_list|,
name|closeDirectory
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|reader
operator|=
operator|new
name|MultiSegmentReader
argument_list|(
name|directory
argument_list|,
name|infos
argument_list|,
name|closeDirectory
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|setDeletionPolicy
argument_list|(
name|deletionPolicy
argument_list|)
expr_stmt|;
return|return
name|reader
return|;
block|}
block|}
decl_stmt|;
if|if
condition|(
name|commit
operator|==
literal|null
condition|)
return|return
operator|(
name|DirectoryIndexReader
operator|)
name|finder
operator|.
name|run
argument_list|()
return|;
else|else
block|{
if|if
condition|(
name|directory
operator|!=
name|commit
operator|.
name|getDirectory
argument_list|()
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"the specified commit does not match the specified Directory"
argument_list|)
throw|;
comment|// This can& will directly throw IOException if the
comment|// specified commit point has been deleted:
return|return
operator|(
name|DirectoryIndexReader
operator|)
name|finder
operator|.
name|doBody
argument_list|(
name|commit
operator|.
name|getSegmentsFileName
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|method|reopen
specifier|public
specifier|final
specifier|synchronized
name|IndexReader
name|reopen
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|hasChanges
operator|||
name|this
operator|.
name|isCurrent
argument_list|()
condition|)
block|{
comment|// this has changes, therefore we have the lock and don't need to reopen
comment|// OR: the index in the directory hasn't changed - nothing to do here
return|return
name|this
return|;
block|}
return|return
operator|(
name|DirectoryIndexReader
operator|)
operator|new
name|SegmentInfos
operator|.
name|FindSegmentsFile
argument_list|(
name|directory
argument_list|)
block|{
specifier|protected
name|Object
name|doBody
parameter_list|(
name|String
name|segmentFileName
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|SegmentInfos
name|infos
init|=
operator|new
name|SegmentInfos
argument_list|()
decl_stmt|;
name|infos
operator|.
name|read
argument_list|(
name|directory
argument_list|,
name|segmentFileName
argument_list|)
expr_stmt|;
name|DirectoryIndexReader
name|newReader
init|=
name|doReopen
argument_list|(
name|infos
argument_list|)
decl_stmt|;
if|if
condition|(
name|DirectoryIndexReader
operator|.
name|this
operator|!=
name|newReader
condition|)
block|{
name|newReader
operator|.
name|init
argument_list|(
name|directory
argument_list|,
name|infos
argument_list|,
name|closeDirectory
argument_list|)
expr_stmt|;
name|newReader
operator|.
name|deletionPolicy
operator|=
name|deletionPolicy
expr_stmt|;
block|}
return|return
name|newReader
return|;
block|}
block|}
operator|.
name|run
argument_list|()
return|;
block|}
comment|/**    * Re-opens the index using the passed-in SegmentInfos     */
DECL|method|doReopen
specifier|protected
specifier|abstract
name|DirectoryIndexReader
name|doReopen
parameter_list|(
name|SegmentInfos
name|infos
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
function_decl|;
DECL|method|setDeletionPolicy
specifier|public
name|void
name|setDeletionPolicy
parameter_list|(
name|IndexDeletionPolicy
name|deletionPolicy
parameter_list|)
block|{
name|this
operator|.
name|deletionPolicy
operator|=
name|deletionPolicy
expr_stmt|;
block|}
comment|/** Returns the directory this index resides in.    */
DECL|method|directory
specifier|public
name|Directory
name|directory
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|directory
return|;
block|}
comment|/**    * Version number when this IndexReader was opened.    */
DECL|method|getVersion
specifier|public
name|long
name|getVersion
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|segmentInfos
operator|.
name|getVersion
argument_list|()
return|;
block|}
comment|/**    * Check whether this IndexReader is still using the    * current (i.e., most recently committed) version of the    * index.  If a writer has committed any changes to the    * index since this reader was opened, this will return    *<code>false</code>, in which case you must open a new    * IndexReader in order to see the changes.  See the    * description of the<a href="IndexWriter.html#autoCommit"><code>autoCommit</code></a>    * flag which controls when the {@link IndexWriter}    * actually commits changes to the index.    *     * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    */
DECL|method|isCurrent
specifier|public
name|boolean
name|isCurrent
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|SegmentInfos
operator|.
name|readCurrentVersion
argument_list|(
name|directory
argument_list|)
operator|==
name|segmentInfos
operator|.
name|getVersion
argument_list|()
return|;
block|}
comment|/**    * Checks is the index is optimized (if it has a single segment and no deletions)    * @return<code>true</code> if the index is optimized;<code>false</code> otherwise    */
DECL|method|isOptimized
specifier|public
name|boolean
name|isOptimized
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|segmentInfos
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|hasDeletions
argument_list|()
operator|==
literal|false
return|;
block|}
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|closeDirectory
condition|)
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Commit changes resulting from delete, undeleteAll, or    * setNorm operations    *    * If an exception is hit, then either no changes or all    * changes will have been committed to the index    * (transactional semantics).    * @throws IOException if there is a low-level IO error    */
DECL|method|doCommit
specifier|protected
name|void
name|doCommit
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|hasChanges
condition|)
block|{
if|if
condition|(
name|segmentInfos
operator|!=
literal|null
condition|)
block|{
comment|// Default deleter (for backwards compatibility) is
comment|// KeepOnlyLastCommitDeleter:
name|IndexFileDeleter
name|deleter
init|=
operator|new
name|IndexFileDeleter
argument_list|(
name|directory
argument_list|,
name|deletionPolicy
operator|==
literal|null
condition|?
operator|new
name|KeepOnlyLastCommitDeletionPolicy
argument_list|()
else|:
name|deletionPolicy
argument_list|,
name|segmentInfos
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// Checkpoint the state we are about to change, in
comment|// case we have to roll back:
name|startCommit
argument_list|()
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|commitChanges
argument_list|()
expr_stmt|;
comment|// Sync all files we just wrote
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|segmentInfos
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|SegmentInfo
name|info
init|=
name|segmentInfos
operator|.
name|info
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|List
name|files
init|=
name|info
operator|.
name|files
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|files
operator|.
name|size
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|String
name|fileName
init|=
operator|(
name|String
operator|)
name|files
operator|.
name|get
argument_list|(
name|j
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|synced
operator|.
name|contains
argument_list|(
name|fileName
argument_list|)
condition|)
block|{
assert|assert
name|directory
operator|.
name|fileExists
argument_list|(
name|fileName
argument_list|)
assert|;
name|directory
operator|.
name|sync
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
name|synced
operator|.
name|add
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|segmentInfos
operator|.
name|commit
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
comment|// Rollback changes that were made to
comment|// SegmentInfos but failed to get [fully]
comment|// committed.  This way this reader instance
comment|// remains consistent (matched to what's
comment|// actually in the index):
name|rollbackCommit
argument_list|()
expr_stmt|;
comment|// Recompute deletable files& remove them (so
comment|// partially written .del files, etc, are
comment|// removed):
name|deleter
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Have the deleter remove any now unreferenced
comment|// files due to this commit:
name|deleter
operator|.
name|checkpoint
argument_list|(
name|segmentInfos
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|writeLock
operator|!=
literal|null
condition|)
block|{
name|writeLock
operator|.
name|release
argument_list|()
expr_stmt|;
comment|// release write lock
name|writeLock
operator|=
literal|null
expr_stmt|;
block|}
block|}
else|else
name|commitChanges
argument_list|()
expr_stmt|;
block|}
name|hasChanges
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|commitChanges
specifier|protected
specifier|abstract
name|void
name|commitChanges
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Tries to acquire the WriteLock on this directory.    * this method is only valid if this IndexReader is directory owner.    *     * @throws StaleReaderException if the index has changed    * since this reader was opened    * @throws CorruptIndexException if the index is corrupt    * @throws LockObtainFailedException if another writer    *  has this index open (<code>write.lock</code> could not    *  be obtained)    * @throws IOException if there is a low-level IO error    */
DECL|method|acquireWriteLock
specifier|protected
name|void
name|acquireWriteLock
parameter_list|()
throws|throws
name|StaleReaderException
throws|,
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
block|{
if|if
condition|(
name|segmentInfos
operator|!=
literal|null
condition|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|stale
condition|)
throw|throw
operator|new
name|StaleReaderException
argument_list|(
literal|"IndexReader out of date and no longer valid for delete, undelete, or setNorm operations"
argument_list|)
throw|;
if|if
condition|(
name|writeLock
operator|==
literal|null
condition|)
block|{
name|Lock
name|writeLock
init|=
name|directory
operator|.
name|makeLock
argument_list|(
name|IndexWriter
operator|.
name|WRITE_LOCK_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|writeLock
operator|.
name|obtain
argument_list|(
name|IndexWriter
operator|.
name|WRITE_LOCK_TIMEOUT
argument_list|)
condition|)
comment|// obtain write lock
throw|throw
operator|new
name|LockObtainFailedException
argument_list|(
literal|"Index locked for write: "
operator|+
name|writeLock
argument_list|)
throw|;
name|this
operator|.
name|writeLock
operator|=
name|writeLock
expr_stmt|;
comment|// we have to check whether index has changed since this reader was opened.
comment|// if so, this reader is no longer valid for deletion
if|if
condition|(
name|SegmentInfos
operator|.
name|readCurrentVersion
argument_list|(
name|directory
argument_list|)
operator|>
name|segmentInfos
operator|.
name|getVersion
argument_list|()
condition|)
block|{
name|stale
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|writeLock
operator|.
name|release
argument_list|()
expr_stmt|;
name|this
operator|.
name|writeLock
operator|=
literal|null
expr_stmt|;
throw|throw
operator|new
name|StaleReaderException
argument_list|(
literal|"IndexReader out of date and no longer valid for delete, undelete, or setNorm operations"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
comment|/**    * Should internally checkpoint state that will change    * during commit so that we can rollback if necessary.    */
DECL|method|startCommit
name|void
name|startCommit
parameter_list|()
block|{
if|if
condition|(
name|segmentInfos
operator|!=
literal|null
condition|)
block|{
name|rollbackSegmentInfos
operator|=
operator|(
name|SegmentInfos
operator|)
name|segmentInfos
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
name|rollbackHasChanges
operator|=
name|hasChanges
expr_stmt|;
block|}
comment|/**    * Rolls back state to just before the commit (this is    * called by commit() if there is some exception while    * committing).    */
DECL|method|rollbackCommit
name|void
name|rollbackCommit
parameter_list|()
block|{
if|if
condition|(
name|segmentInfos
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|segmentInfos
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
comment|// Rollback each segmentInfo.  Because the
comment|// SegmentReader holds a reference to the
comment|// SegmentInfo we can't [easily] just replace
comment|// segmentInfos, so we reset it in place instead:
name|segmentInfos
operator|.
name|info
argument_list|(
name|i
argument_list|)
operator|.
name|reset
argument_list|(
name|rollbackSegmentInfos
operator|.
name|info
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|rollbackSegmentInfos
operator|=
literal|null
expr_stmt|;
block|}
name|hasChanges
operator|=
name|rollbackHasChanges
expr_stmt|;
block|}
comment|/** Release the write lock, if needed. */
DECL|method|finalize
specifier|protected
name|void
name|finalize
parameter_list|()
throws|throws
name|Throwable
block|{
try|try
block|{
if|if
condition|(
name|writeLock
operator|!=
literal|null
condition|)
block|{
name|writeLock
operator|.
name|release
argument_list|()
expr_stmt|;
comment|// release write lock
name|writeLock
operator|=
literal|null
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|super
operator|.
name|finalize
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|ReaderCommit
specifier|private
specifier|static
class|class
name|ReaderCommit
extends|extends
name|IndexCommit
block|{
DECL|field|segmentsFileName
specifier|private
name|String
name|segmentsFileName
decl_stmt|;
DECL|field|files
name|Collection
name|files
decl_stmt|;
DECL|field|dir
name|Directory
name|dir
decl_stmt|;
DECL|field|generation
name|long
name|generation
decl_stmt|;
DECL|field|version
name|long
name|version
decl_stmt|;
DECL|field|isOptimized
specifier|final
name|boolean
name|isOptimized
decl_stmt|;
DECL|method|ReaderCommit
name|ReaderCommit
parameter_list|(
name|SegmentInfos
name|infos
parameter_list|,
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|segmentsFileName
operator|=
name|infos
operator|.
name|getCurrentSegmentFileName
argument_list|()
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
specifier|final
name|int
name|size
init|=
name|infos
operator|.
name|size
argument_list|()
decl_stmt|;
name|files
operator|=
operator|new
name|ArrayList
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|files
operator|.
name|add
argument_list|(
name|segmentsFileName
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|SegmentInfo
name|info
init|=
name|infos
operator|.
name|info
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|dir
operator|==
name|dir
condition|)
name|files
operator|.
name|addAll
argument_list|(
name|info
operator|.
name|files
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|version
operator|=
name|infos
operator|.
name|getVersion
argument_list|()
expr_stmt|;
name|generation
operator|=
name|infos
operator|.
name|getGeneration
argument_list|()
expr_stmt|;
name|isOptimized
operator|=
name|infos
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
operator|!
name|infos
operator|.
name|info
argument_list|(
literal|0
argument_list|)
operator|.
name|hasDeletions
argument_list|()
expr_stmt|;
block|}
DECL|method|isOptimized
specifier|public
name|boolean
name|isOptimized
parameter_list|()
block|{
return|return
name|isOptimized
return|;
block|}
DECL|method|getSegmentsFileName
specifier|public
name|String
name|getSegmentsFileName
parameter_list|()
block|{
return|return
name|segmentsFileName
return|;
block|}
DECL|method|getFileNames
specifier|public
name|Collection
name|getFileNames
parameter_list|()
block|{
return|return
name|files
return|;
block|}
DECL|method|getDirectory
specifier|public
name|Directory
name|getDirectory
parameter_list|()
block|{
return|return
name|dir
return|;
block|}
DECL|method|getVersion
specifier|public
name|long
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
DECL|method|getGeneration
specifier|public
name|long
name|getGeneration
parameter_list|()
block|{
return|return
name|generation
return|;
block|}
block|}
comment|/**    * Expert: return the IndexCommit that this reader has    * opened.    *    *<p><b>WARNING</b>: this API is new and experimental and    * may suddenly change.</p>    */
DECL|method|getIndexCommit
specifier|public
name|IndexCommit
name|getIndexCommit
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|ReaderCommit
argument_list|(
name|segmentInfos
argument_list|,
name|directory
argument_list|)
return|;
block|}
comment|/** @see IndexReader#listCommits */
DECL|method|listCommits
specifier|public
specifier|static
name|Collection
name|listCommits
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
index|[]
name|files
init|=
name|dir
operator|.
name|list
argument_list|()
decl_stmt|;
if|if
condition|(
name|files
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"cannot read directory "
operator|+
name|dir
operator|+
literal|": list() returned null"
argument_list|)
throw|;
name|Collection
name|commits
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|SegmentInfos
name|latest
init|=
operator|new
name|SegmentInfos
argument_list|()
decl_stmt|;
name|latest
operator|.
name|read
argument_list|(
name|dir
argument_list|)
expr_stmt|;
specifier|final
name|long
name|currentGen
init|=
name|latest
operator|.
name|getGeneration
argument_list|()
decl_stmt|;
name|commits
operator|.
name|add
argument_list|(
operator|new
name|ReaderCommit
argument_list|(
name|latest
argument_list|,
name|dir
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|fileName
init|=
name|files
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|fileName
operator|.
name|startsWith
argument_list|(
name|IndexFileNames
operator|.
name|SEGMENTS
argument_list|)
operator|&&
operator|!
name|fileName
operator|.
name|equals
argument_list|(
name|IndexFileNames
operator|.
name|SEGMENTS_GEN
argument_list|)
operator|&&
name|SegmentInfos
operator|.
name|generationFromSegmentsFileName
argument_list|(
name|fileName
argument_list|)
operator|<
name|currentGen
condition|)
block|{
name|SegmentInfos
name|sis
init|=
operator|new
name|SegmentInfos
argument_list|()
decl_stmt|;
try|try
block|{
comment|// IOException allowed to throw there, in case
comment|// segments_N is corrupt
name|sis
operator|.
name|read
argument_list|(
name|dir
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|fnfe
parameter_list|)
block|{
comment|// LUCENE-948: on NFS (and maybe others), if
comment|// you have writers switching back and forth
comment|// between machines, it's very likely that the
comment|// dir listing will be stale and will claim a
comment|// file segments_X exists when in fact it
comment|// doesn't.  So, we catch this and handle it
comment|// as if the file does not exist
name|sis
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|sis
operator|!=
literal|null
condition|)
name|commits
operator|.
name|add
argument_list|(
operator|new
name|ReaderCommit
argument_list|(
name|sis
argument_list|,
name|dir
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|commits
return|;
block|}
block|}
end_class
end_unit
