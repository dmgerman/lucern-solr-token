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
name|io
operator|.
name|PrintStream
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
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|ArrayList
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
name|Collection
import|;
end_import
begin_comment
comment|/*  * This class keeps track of each SegmentInfos instance that  * is still "live", either because it corresponds to a  * segments_N file in the Directory (a "commit", i.e. a  * committed SegmentInfos) or because it's an in-memory  * SegmentInfos that a writer is actively updating but has  * not yet committed.  This class uses simple reference  * counting to map the live SegmentInfos instances to  * individual files in the Directory.  *  * When autoCommit=true, IndexWriter currently commits only  * on completion of a merge (though this may change with  * time: it is not a guarantee).  When autoCommit=false,  * IndexWriter only commits when it is closed.  Regardless  * of autoCommit, the user may call IndexWriter.commit() to  * force a blocking commit.  *   * The same directory file may be referenced by more than  * one IndexCommitPoints, i.e. more than one SegmentInfos.  * Therefore we count how many commits reference each file.  * When all the commits referencing a certain file have been  * deleted, the refcount for that file becomes zero, and the  * file is deleted.  *  * A separate deletion policy interface  * (IndexDeletionPolicy) is consulted on creation (onInit)  * and once per commit (onCommit), to decide when a commit  * should be removed.  *   * It is the business of the IndexDeletionPolicy to choose  * when to delete commit points.  The actual mechanics of  * file deletion, retrying, etc, derived from the deletion  * of commit points is the business of the IndexFileDeleter.  *   * The current default deletion policy is {@link  * KeepOnlyLastCommitDeletionPolicy}, which removes all  * prior commits when a new commit has completed.  This  * matches the behavior before 2.2.  *  * Note that you must hold the write.lock before  * instantiating this class.  It opens segments_N file(s)  * directly with no retry logic.  */
end_comment
begin_class
DECL|class|IndexFileDeleter
specifier|final
class|class
name|IndexFileDeleter
block|{
comment|/* Files that we tried to delete but failed (likely    * because they are open and we are running on Windows),    * so we will retry them again later: */
DECL|field|deletable
specifier|private
name|List
name|deletable
decl_stmt|;
comment|/* Reference count for all files in the index.      * Counts how many existing commits reference a file.    * Maps String to RefCount (class below) instances: */
DECL|field|refCounts
specifier|private
name|Map
name|refCounts
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
comment|/* Holds all commits (segments_N) currently in the index.    * This will have just 1 commit if you are using the    * default delete policy (KeepOnlyLastCommitDeletionPolicy).    * Other policies may leave commit points live for longer    * in which case this list would be longer than 1: */
DECL|field|commits
specifier|private
name|List
name|commits
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
comment|/* Holds files we had incref'd from the previous    * non-commit checkpoint: */
DECL|field|lastFiles
specifier|private
name|List
name|lastFiles
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
comment|/* Commits that the IndexDeletionPolicy have decided to delete: */
DECL|field|commitsToDelete
specifier|private
name|List
name|commitsToDelete
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
DECL|field|infoStream
specifier|private
name|PrintStream
name|infoStream
decl_stmt|;
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|field|policy
specifier|private
name|IndexDeletionPolicy
name|policy
decl_stmt|;
DECL|field|docWriter
specifier|private
name|DocumentsWriter
name|docWriter
decl_stmt|;
comment|/** Change to true to see details of reference counts when    *  infoStream != null */
DECL|field|VERBOSE_REF_COUNTS
specifier|public
specifier|static
name|boolean
name|VERBOSE_REF_COUNTS
init|=
literal|false
decl_stmt|;
DECL|method|setInfoStream
name|void
name|setInfoStream
parameter_list|(
name|PrintStream
name|infoStream
parameter_list|)
block|{
name|this
operator|.
name|infoStream
operator|=
name|infoStream
expr_stmt|;
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
name|message
argument_list|(
literal|"setInfoStream deletionPolicy="
operator|+
name|policy
argument_list|)
expr_stmt|;
block|}
DECL|method|message
specifier|private
name|void
name|message
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|infoStream
operator|.
name|println
argument_list|(
literal|"IFD ["
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"]: "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
comment|/**    * Initialize the deleter: find all previous commits in    * the Directory, incref the files they reference, call    * the policy to let it delete commits.  The incoming    * segmentInfos must have been loaded from a commit point    * and not yet modified.  This will remove any files not    * referenced by any of the commits.    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    */
DECL|method|IndexFileDeleter
specifier|public
name|IndexFileDeleter
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|IndexDeletionPolicy
name|policy
parameter_list|,
name|SegmentInfos
name|segmentInfos
parameter_list|,
name|PrintStream
name|infoStream
parameter_list|,
name|DocumentsWriter
name|docWriter
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|this
operator|.
name|docWriter
operator|=
name|docWriter
expr_stmt|;
name|this
operator|.
name|infoStream
operator|=
name|infoStream
expr_stmt|;
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
name|message
argument_list|(
literal|"init: current segments file is \""
operator|+
name|segmentInfos
operator|.
name|getCurrentSegmentFileName
argument_list|()
operator|+
literal|"\"; deletionPolicy="
operator|+
name|policy
argument_list|)
expr_stmt|;
name|this
operator|.
name|policy
operator|=
name|policy
expr_stmt|;
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
comment|// First pass: walk the files and initialize our ref
comment|// counts:
name|long
name|currentGen
init|=
name|segmentInfos
operator|.
name|getGeneration
argument_list|()
decl_stmt|;
name|IndexFileNameFilter
name|filter
init|=
name|IndexFileNameFilter
operator|.
name|getFilter
argument_list|()
decl_stmt|;
name|String
index|[]
name|files
init|=
name|directory
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
name|directory
operator|+
literal|": list() returned null"
argument_list|)
throw|;
name|CommitPoint
name|currentCommitPoint
init|=
literal|null
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
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
name|filter
operator|.
name|accept
argument_list|(
literal|null
argument_list|,
name|fileName
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
condition|)
block|{
comment|// Add this file to refCounts with initial count 0:
name|getRefCount
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
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
condition|)
block|{
comment|// This is a commit (segments or segments_N), and
comment|// it's valid (<= the max gen).  Load it, then
comment|// incref all files it refers to:
if|if
condition|(
name|SegmentInfos
operator|.
name|generationFromSegmentsFileName
argument_list|(
name|fileName
argument_list|)
operator|<=
name|currentGen
condition|)
block|{
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"init: load commit \""
operator|+
name|fileName
operator|+
literal|"\""
argument_list|)
expr_stmt|;
block|}
name|SegmentInfos
name|sis
init|=
operator|new
name|SegmentInfos
argument_list|()
decl_stmt|;
try|try
block|{
name|sis
operator|.
name|read
argument_list|(
name|directory
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
comment|// LUCENE-948: on NFS (and maybe others), if
comment|// you have writers switching back and forth
comment|// between machines, it's very likely that the
comment|// dir listing will be stale and will claim a
comment|// file segments_X exists when in fact it
comment|// doesn't.  So, we catch this and handle it
comment|// as if the file does not exist
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"init: hit FileNotFoundException when loading commit \""
operator|+
name|fileName
operator|+
literal|"\"; skipping this commit point"
argument_list|)
expr_stmt|;
block|}
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
block|{
name|CommitPoint
name|commitPoint
init|=
operator|new
name|CommitPoint
argument_list|(
name|sis
argument_list|)
decl_stmt|;
if|if
condition|(
name|sis
operator|.
name|getGeneration
argument_list|()
operator|==
name|segmentInfos
operator|.
name|getGeneration
argument_list|()
condition|)
block|{
name|currentCommitPoint
operator|=
name|commitPoint
expr_stmt|;
block|}
name|commits
operator|.
name|add
argument_list|(
name|commitPoint
argument_list|)
expr_stmt|;
name|incRef
argument_list|(
name|sis
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
if|if
condition|(
name|currentCommitPoint
operator|==
literal|null
condition|)
block|{
comment|// We did not in fact see the segments_N file
comment|// corresponding to the segmentInfos that was passed
comment|// in.  Yet, it must exist, because our caller holds
comment|// the write lock.  This can happen when the directory
comment|// listing was stale (eg when index accessed via NFS
comment|// client with stale directory listing cache).  So we
comment|// try now to explicitly open this commit point:
name|SegmentInfos
name|sis
init|=
operator|new
name|SegmentInfos
argument_list|()
decl_stmt|;
try|try
block|{
name|sis
operator|.
name|read
argument_list|(
name|directory
argument_list|,
name|segmentInfos
operator|.
name|getCurrentSegmentFileName
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
name|CorruptIndexException
argument_list|(
literal|"failed to locate current segments_N file"
argument_list|)
throw|;
block|}
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
name|message
argument_list|(
literal|"forced open of current segments file "
operator|+
name|segmentInfos
operator|.
name|getCurrentSegmentFileName
argument_list|()
argument_list|)
expr_stmt|;
name|currentCommitPoint
operator|=
operator|new
name|CommitPoint
argument_list|(
name|sis
argument_list|)
expr_stmt|;
name|commits
operator|.
name|add
argument_list|(
name|currentCommitPoint
argument_list|)
expr_stmt|;
name|incRef
argument_list|(
name|sis
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// We keep commits list in sorted order (oldest to newest):
name|Collections
operator|.
name|sort
argument_list|(
name|commits
argument_list|)
expr_stmt|;
comment|// Now delete anything with ref count at 0.  These are
comment|// presumably abandoned files eg due to crash of
comment|// IndexWriter.
name|Iterator
name|it
init|=
name|refCounts
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|fileName
init|=
operator|(
name|String
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|RefCount
name|rc
init|=
operator|(
name|RefCount
operator|)
name|refCounts
operator|.
name|get
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0
operator|==
name|rc
operator|.
name|count
condition|)
block|{
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"init: removing unreferenced file \""
operator|+
name|fileName
operator|+
literal|"\""
argument_list|)
expr_stmt|;
block|}
name|deleteFile
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Finally, give policy a chance to remove things on
comment|// startup:
name|policy
operator|.
name|onInit
argument_list|(
name|commits
argument_list|)
expr_stmt|;
comment|// It's OK for the onInit to remove the current commit
comment|// point; we just have to checkpoint our in-memory
comment|// SegmentInfos to protect those files that it uses:
if|if
condition|(
name|currentCommitPoint
operator|.
name|deleted
condition|)
block|{
name|checkpoint
argument_list|(
name|segmentInfos
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|deleteCommits
argument_list|()
expr_stmt|;
block|}
comment|/**    * Remove the CommitPoints in the commitsToDelete List by    * DecRef'ing all files from each SegmentInfos.    */
DECL|method|deleteCommits
specifier|private
name|void
name|deleteCommits
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|size
init|=
name|commitsToDelete
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|>
literal|0
condition|)
block|{
comment|// First decref all files that had been referred to by
comment|// the now-deleted commits:
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
name|CommitPoint
name|commit
init|=
operator|(
name|CommitPoint
operator|)
name|commitsToDelete
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"deleteCommits: now decRef commit \""
operator|+
name|commit
operator|.
name|getSegmentsFileName
argument_list|()
operator|+
literal|"\""
argument_list|)
expr_stmt|;
block|}
name|int
name|size2
init|=
name|commit
operator|.
name|files
operator|.
name|size
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
name|size2
condition|;
name|j
operator|++
control|)
block|{
name|decRef
argument_list|(
operator|(
name|String
operator|)
name|commit
operator|.
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
name|commitsToDelete
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// Now compact commits to remove deleted ones (preserving the sort):
name|size
operator|=
name|commits
operator|.
name|size
argument_list|()
expr_stmt|;
name|int
name|readFrom
init|=
literal|0
decl_stmt|;
name|int
name|writeTo
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|readFrom
operator|<
name|size
condition|)
block|{
name|CommitPoint
name|commit
init|=
operator|(
name|CommitPoint
operator|)
name|commits
operator|.
name|get
argument_list|(
name|readFrom
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|commit
operator|.
name|deleted
condition|)
block|{
if|if
condition|(
name|writeTo
operator|!=
name|readFrom
condition|)
block|{
name|commits
operator|.
name|set
argument_list|(
name|writeTo
argument_list|,
name|commits
operator|.
name|get
argument_list|(
name|readFrom
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writeTo
operator|++
expr_stmt|;
block|}
name|readFrom
operator|++
expr_stmt|;
block|}
while|while
condition|(
name|size
operator|>
name|writeTo
condition|)
block|{
name|commits
operator|.
name|remove
argument_list|(
name|size
operator|-
literal|1
argument_list|)
expr_stmt|;
name|size
operator|--
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Writer calls this when it has hit an error and had to    * roll back, to tell us that there may now be    * unreferenced files in the filesystem.  So we re-list    * the filesystem and delete such files.  If segmentName    * is non-null, we will only delete files corresponding to    * that segment.    */
DECL|method|refresh
specifier|public
name|void
name|refresh
parameter_list|(
name|String
name|segmentName
parameter_list|)
throws|throws
name|IOException
block|{
name|String
index|[]
name|files
init|=
name|directory
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
name|directory
operator|+
literal|": list() returned null"
argument_list|)
throw|;
name|IndexFileNameFilter
name|filter
init|=
name|IndexFileNameFilter
operator|.
name|getFilter
argument_list|()
decl_stmt|;
name|String
name|segmentPrefix1
decl_stmt|;
name|String
name|segmentPrefix2
decl_stmt|;
if|if
condition|(
name|segmentName
operator|!=
literal|null
condition|)
block|{
name|segmentPrefix1
operator|=
name|segmentName
operator|+
literal|"."
expr_stmt|;
name|segmentPrefix2
operator|=
name|segmentName
operator|+
literal|"_"
expr_stmt|;
block|}
else|else
block|{
name|segmentPrefix1
operator|=
literal|null
expr_stmt|;
name|segmentPrefix2
operator|=
literal|null
expr_stmt|;
block|}
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
name|filter
operator|.
name|accept
argument_list|(
literal|null
argument_list|,
name|fileName
argument_list|)
operator|&&
operator|(
name|segmentName
operator|==
literal|null
operator|||
name|fileName
operator|.
name|startsWith
argument_list|(
name|segmentPrefix1
argument_list|)
operator|||
name|fileName
operator|.
name|startsWith
argument_list|(
name|segmentPrefix2
argument_list|)
operator|)
operator|&&
operator|!
name|refCounts
operator|.
name|containsKey
argument_list|(
name|fileName
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
condition|)
block|{
comment|// Unreferenced file, so remove it
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"refresh [prefix="
operator|+
name|segmentName
operator|+
literal|"]: removing newly created unreferenced file \""
operator|+
name|fileName
operator|+
literal|"\""
argument_list|)
expr_stmt|;
block|}
name|deleteFile
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|refresh
specifier|public
name|void
name|refresh
parameter_list|()
throws|throws
name|IOException
block|{
name|refresh
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|deletePendingFiles
argument_list|()
expr_stmt|;
block|}
DECL|method|deletePendingFiles
specifier|private
name|void
name|deletePendingFiles
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|deletable
operator|!=
literal|null
condition|)
block|{
name|List
name|oldDeletable
init|=
name|deletable
decl_stmt|;
name|deletable
operator|=
literal|null
expr_stmt|;
name|int
name|size
init|=
name|oldDeletable
operator|.
name|size
argument_list|()
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
name|size
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
name|message
argument_list|(
literal|"delete pending file "
operator|+
name|oldDeletable
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|deleteFile
argument_list|(
operator|(
name|String
operator|)
name|oldDeletable
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * For definition of "check point" see IndexWriter comments:    * "Clarification: Check Points (and commits)".    *     * Writer calls this when it has made a "consistent    * change" to the index, meaning new files are written to    * the index and the in-memory SegmentInfos have been    * modified to point to those files.    *    * This may or may not be a commit (segments_N may or may    * not have been written).    *    * We simply incref the files referenced by the new    * SegmentInfos and decref the files we had previously    * seen (if any).    *    * If this is a commit, we also call the policy to give it    * a chance to remove other commits.  If any commits are    * removed, we decref their files as well.    */
DECL|method|checkpoint
specifier|public
name|void
name|checkpoint
parameter_list|(
name|SegmentInfos
name|segmentInfos
parameter_list|,
name|boolean
name|isCommit
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"now checkpoint \""
operator|+
name|segmentInfos
operator|.
name|getCurrentSegmentFileName
argument_list|()
operator|+
literal|"\" ["
operator|+
name|segmentInfos
operator|.
name|size
argument_list|()
operator|+
literal|" segments "
operator|+
literal|"; isCommit = "
operator|+
name|isCommit
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
comment|// Try again now to delete any previously un-deletable
comment|// files (because they were in use, on Windows):
name|deletePendingFiles
argument_list|()
expr_stmt|;
comment|// Incref the files:
name|incRef
argument_list|(
name|segmentInfos
argument_list|,
name|isCommit
argument_list|)
expr_stmt|;
if|if
condition|(
name|isCommit
condition|)
block|{
comment|// Append to our commits list:
name|commits
operator|.
name|add
argument_list|(
operator|new
name|CommitPoint
argument_list|(
name|segmentInfos
argument_list|)
argument_list|)
expr_stmt|;
comment|// Tell policy so it can remove commits:
name|policy
operator|.
name|onCommit
argument_list|(
name|commits
argument_list|)
expr_stmt|;
comment|// Decref files for commits that were deleted by the policy:
name|deleteCommits
argument_list|()
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|List
name|docWriterFiles
decl_stmt|;
if|if
condition|(
name|docWriter
operator|!=
literal|null
condition|)
block|{
name|docWriterFiles
operator|=
name|docWriter
operator|.
name|files
argument_list|()
expr_stmt|;
if|if
condition|(
name|docWriterFiles
operator|!=
literal|null
condition|)
comment|// We must incRef thes files before decRef'ing
comment|// last files to make sure we don't accidentally
comment|// delete them:
name|incRef
argument_list|(
name|docWriterFiles
argument_list|)
expr_stmt|;
block|}
else|else
name|docWriterFiles
operator|=
literal|null
expr_stmt|;
comment|// DecRef old files from the last checkpoint, if any:
name|int
name|size
init|=
name|lastFiles
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|>
literal|0
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
name|size
condition|;
name|i
operator|++
control|)
name|decRef
argument_list|(
operator|(
name|List
operator|)
name|lastFiles
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|lastFiles
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|// Save files so we can decr on next checkpoint/commit:
name|size
operator|=
name|segmentInfos
operator|.
name|size
argument_list|()
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
name|segmentInfo
init|=
name|segmentInfos
operator|.
name|info
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|segmentInfo
operator|.
name|dir
operator|==
name|directory
condition|)
block|{
name|lastFiles
operator|.
name|add
argument_list|(
name|segmentInfo
operator|.
name|files
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|docWriterFiles
operator|!=
literal|null
condition|)
name|lastFiles
operator|.
name|add
argument_list|(
name|docWriterFiles
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|incRef
name|void
name|incRef
parameter_list|(
name|SegmentInfos
name|segmentInfos
parameter_list|,
name|boolean
name|isCommit
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|size
init|=
name|segmentInfos
operator|.
name|size
argument_list|()
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|SegmentInfo
name|segmentInfo
init|=
name|segmentInfos
operator|.
name|info
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|segmentInfo
operator|.
name|dir
operator|==
name|directory
condition|)
block|{
name|incRef
argument_list|(
name|segmentInfo
operator|.
name|files
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|isCommit
condition|)
block|{
comment|// Since this is a commit point, also incref its
comment|// segments_N file:
name|getRefCount
argument_list|(
name|segmentInfos
operator|.
name|getCurrentSegmentFileName
argument_list|()
argument_list|)
operator|.
name|IncRef
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|incRef
name|void
name|incRef
parameter_list|(
name|List
name|files
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|size
init|=
name|files
operator|.
name|size
argument_list|()
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
name|size
condition|;
name|i
operator|++
control|)
block|{
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
name|i
argument_list|)
decl_stmt|;
name|RefCount
name|rc
init|=
name|getRefCount
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
if|if
condition|(
name|infoStream
operator|!=
literal|null
operator|&&
name|VERBOSE_REF_COUNTS
condition|)
block|{
name|message
argument_list|(
literal|"  IncRef \""
operator|+
name|fileName
operator|+
literal|"\": pre-incr count is "
operator|+
name|rc
operator|.
name|count
argument_list|)
expr_stmt|;
block|}
name|rc
operator|.
name|IncRef
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|decRef
name|void
name|decRef
parameter_list|(
name|List
name|files
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|size
init|=
name|files
operator|.
name|size
argument_list|()
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|decRef
argument_list|(
operator|(
name|String
operator|)
name|files
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|decRef
name|void
name|decRef
parameter_list|(
name|String
name|fileName
parameter_list|)
throws|throws
name|IOException
block|{
name|RefCount
name|rc
init|=
name|getRefCount
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
if|if
condition|(
name|infoStream
operator|!=
literal|null
operator|&&
name|VERBOSE_REF_COUNTS
condition|)
block|{
name|message
argument_list|(
literal|"  DecRef \""
operator|+
name|fileName
operator|+
literal|"\": pre-decr count is "
operator|+
name|rc
operator|.
name|count
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|0
operator|==
name|rc
operator|.
name|DecRef
argument_list|()
condition|)
block|{
comment|// This file is no longer referenced by any past
comment|// commit points nor by the in-memory SegmentInfos:
name|deleteFile
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
name|refCounts
operator|.
name|remove
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|decRef
name|void
name|decRef
parameter_list|(
name|SegmentInfos
name|segmentInfos
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|size
init|=
name|segmentInfos
operator|.
name|size
argument_list|()
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|SegmentInfo
name|segmentInfo
init|=
name|segmentInfos
operator|.
name|info
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|segmentInfo
operator|.
name|dir
operator|==
name|directory
condition|)
block|{
name|decRef
argument_list|(
name|segmentInfo
operator|.
name|files
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getRefCount
specifier|private
name|RefCount
name|getRefCount
parameter_list|(
name|String
name|fileName
parameter_list|)
block|{
name|RefCount
name|rc
decl_stmt|;
if|if
condition|(
operator|!
name|refCounts
operator|.
name|containsKey
argument_list|(
name|fileName
argument_list|)
condition|)
block|{
name|rc
operator|=
operator|new
name|RefCount
argument_list|()
expr_stmt|;
name|refCounts
operator|.
name|put
argument_list|(
name|fileName
argument_list|,
name|rc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rc
operator|=
operator|(
name|RefCount
operator|)
name|refCounts
operator|.
name|get
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
DECL|method|deleteFiles
name|void
name|deleteFiles
parameter_list|(
name|List
name|files
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|size
init|=
name|files
operator|.
name|size
argument_list|()
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
name|size
condition|;
name|i
operator|++
control|)
name|deleteFile
argument_list|(
operator|(
name|String
operator|)
name|files
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Delets the specified files, but only if they are new    *  (have not yet been incref'd). */
DECL|method|deleteNewFiles
name|void
name|deleteNewFiles
parameter_list|(
name|List
name|files
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|size
init|=
name|files
operator|.
name|size
argument_list|()
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
name|size
condition|;
name|i
operator|++
control|)
if|if
condition|(
operator|!
name|refCounts
operator|.
name|containsKey
argument_list|(
name|files
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
name|deleteFile
argument_list|(
operator|(
name|String
operator|)
name|files
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteFile
name|void
name|deleteFile
parameter_list|(
name|String
name|fileName
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"delete \""
operator|+
name|fileName
operator|+
literal|"\""
argument_list|)
expr_stmt|;
block|}
name|directory
operator|.
name|deleteFile
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// if delete fails
if|if
condition|(
name|directory
operator|.
name|fileExists
argument_list|(
name|fileName
argument_list|)
condition|)
block|{
comment|// Some operating systems (e.g. Windows) don't
comment|// permit a file to be deleted while it is opened
comment|// for read (e.g. by another process or thread). So
comment|// we assume that when a delete fails it is because
comment|// the file is open in another process, and queue
comment|// the file for subsequent deletion.
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"IndexFileDeleter: unable to remove file \""
operator|+
name|fileName
operator|+
literal|"\": "
operator|+
name|e
operator|.
name|toString
argument_list|()
operator|+
literal|"; Will re-try later."
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|deletable
operator|==
literal|null
condition|)
block|{
name|deletable
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
block|}
name|deletable
operator|.
name|add
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
comment|// add to deletable
block|}
block|}
block|}
comment|/**    * Tracks the reference count for a single index file:    */
DECL|class|RefCount
specifier|final
specifier|private
specifier|static
class|class
name|RefCount
block|{
DECL|field|count
name|int
name|count
decl_stmt|;
DECL|method|IncRef
specifier|public
name|int
name|IncRef
parameter_list|()
block|{
return|return
operator|++
name|count
return|;
block|}
DECL|method|DecRef
specifier|public
name|int
name|DecRef
parameter_list|()
block|{
assert|assert
name|count
operator|>
literal|0
assert|;
return|return
operator|--
name|count
return|;
block|}
block|}
comment|/**    * Holds details for each commit point.  This class is    * also passed to the deletion policy.  Note: this class    * has a natural ordering that is inconsistent with    * equals.    */
DECL|class|CommitPoint
specifier|final
specifier|private
class|class
name|CommitPoint
implements|implements
name|Comparable
implements|,
name|IndexCommitPoint
block|{
DECL|field|gen
name|long
name|gen
decl_stmt|;
DECL|field|files
name|List
name|files
decl_stmt|;
DECL|field|segmentsFileName
name|String
name|segmentsFileName
decl_stmt|;
DECL|field|deleted
name|boolean
name|deleted
decl_stmt|;
DECL|method|CommitPoint
specifier|public
name|CommitPoint
parameter_list|(
name|SegmentInfos
name|segmentInfos
parameter_list|)
throws|throws
name|IOException
block|{
name|segmentsFileName
operator|=
name|segmentInfos
operator|.
name|getCurrentSegmentFileName
argument_list|()
expr_stmt|;
name|int
name|size
init|=
name|segmentInfos
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
name|gen
operator|=
name|segmentInfos
operator|.
name|getGeneration
argument_list|()
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
name|segmentInfo
init|=
name|segmentInfos
operator|.
name|info
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|segmentInfo
operator|.
name|dir
operator|==
name|directory
condition|)
block|{
name|files
operator|.
name|addAll
argument_list|(
name|segmentInfo
operator|.
name|files
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Get the segments_N file for this commit point.      */
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
throws|throws
name|IOException
block|{
return|return
name|Collections
operator|.
name|unmodifiableCollection
argument_list|(
name|files
argument_list|)
return|;
block|}
comment|/**      * Called only be the deletion policy, to remove this      * commit point from the index.      */
DECL|method|delete
specifier|public
name|void
name|delete
parameter_list|()
block|{
if|if
condition|(
operator|!
name|deleted
condition|)
block|{
name|deleted
operator|=
literal|true
expr_stmt|;
name|commitsToDelete
operator|.
name|add
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
name|CommitPoint
name|commit
init|=
operator|(
name|CommitPoint
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|gen
operator|<
name|commit
operator|.
name|gen
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|gen
operator|>
name|commit
operator|.
name|gen
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
block|}
block|}
end_class
end_unit
