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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|ArrayList
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
name|io
operator|.
name|IOException
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
begin_comment
comment|/**  * An {@link IndexDeletionPolicy} that wraps any other  * {@link IndexDeletionPolicy} and adds the ability to hold and later release  * snapshots of an index. While a snapshot is held, the {@link IndexWriter} will  * not remove any files associated with it even if the index is otherwise being  * actively, arbitrarily changed. Because we wrap another arbitrary  * {@link IndexDeletionPolicy}, this gives you the freedom to continue using  * whatever {@link IndexDeletionPolicy} you would normally want to use with your  * index.  *   *<p>  * This class maintains all snapshots in-memory, and so the information is not  * persisted and not protected against system failures. If persistence is  * important, you can use {@link PersistentSnapshotDeletionPolicy}.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|SnapshotDeletionPolicy
specifier|public
class|class
name|SnapshotDeletionPolicy
extends|extends
name|IndexDeletionPolicy
block|{
comment|/** Records how many snapshots are held against each    *  commit generation */
DECL|field|refCounts
specifier|protected
name|Map
argument_list|<
name|Long
argument_list|,
name|Integer
argument_list|>
name|refCounts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** Used to map gen to IndexCommit. */
DECL|field|indexCommits
specifier|protected
name|Map
argument_list|<
name|Long
argument_list|,
name|IndexCommit
argument_list|>
name|indexCommits
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** Wrapped {@link IndexDeletionPolicy} */
DECL|field|primary
specifier|private
name|IndexDeletionPolicy
name|primary
decl_stmt|;
comment|/** Most recently committed {@link IndexCommit}. */
DECL|field|lastCommit
specifier|protected
name|IndexCommit
name|lastCommit
decl_stmt|;
comment|/** Used to detect misuse */
DECL|field|initCalled
specifier|private
name|boolean
name|initCalled
decl_stmt|;
comment|/** Sole constructor, taking the incoming {@link    *  IndexDeletionPolicy} to wrap. */
DECL|method|SnapshotDeletionPolicy
specifier|public
name|SnapshotDeletionPolicy
parameter_list|(
name|IndexDeletionPolicy
name|primary
parameter_list|)
block|{
name|this
operator|.
name|primary
operator|=
name|primary
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onCommit
specifier|public
specifier|synchronized
name|void
name|onCommit
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|IndexCommit
argument_list|>
name|commits
parameter_list|)
throws|throws
name|IOException
block|{
name|primary
operator|.
name|onCommit
argument_list|(
name|wrapCommits
argument_list|(
name|commits
argument_list|)
argument_list|)
expr_stmt|;
name|lastCommit
operator|=
name|commits
operator|.
name|get
argument_list|(
name|commits
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onInit
specifier|public
specifier|synchronized
name|void
name|onInit
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|IndexCommit
argument_list|>
name|commits
parameter_list|)
throws|throws
name|IOException
block|{
name|initCalled
operator|=
literal|true
expr_stmt|;
name|primary
operator|.
name|onInit
argument_list|(
name|wrapCommits
argument_list|(
name|commits
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|IndexCommit
name|commit
range|:
name|commits
control|)
block|{
if|if
condition|(
name|refCounts
operator|.
name|containsKey
argument_list|(
name|commit
operator|.
name|getGeneration
argument_list|()
argument_list|)
condition|)
block|{
name|indexCommits
operator|.
name|put
argument_list|(
name|commit
operator|.
name|getGeneration
argument_list|()
argument_list|,
name|commit
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|commits
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|lastCommit
operator|=
name|commits
operator|.
name|get
argument_list|(
name|commits
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Release a snapshotted commit.    *     * @param commit    *          the commit previously returned by {@link #snapshot}    */
DECL|method|release
specifier|public
specifier|synchronized
name|void
name|release
parameter_list|(
name|IndexCommit
name|commit
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|gen
init|=
name|commit
operator|.
name|getGeneration
argument_list|()
decl_stmt|;
name|releaseGen
argument_list|(
name|gen
argument_list|)
expr_stmt|;
block|}
comment|/** Release a snapshot by generation. */
DECL|method|releaseGen
specifier|protected
name|void
name|releaseGen
parameter_list|(
name|long
name|gen
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|initCalled
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"this instance is not being used by IndexWriter; be sure to use the instance returned from writer.getConfig().getIndexDeletionPolicy()"
argument_list|)
throw|;
block|}
name|Integer
name|refCount
init|=
name|refCounts
operator|.
name|get
argument_list|(
name|gen
argument_list|)
decl_stmt|;
if|if
condition|(
name|refCount
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"commit gen="
operator|+
name|gen
operator|+
literal|" is not currently snapshotted"
argument_list|)
throw|;
block|}
name|int
name|refCountInt
init|=
name|refCount
operator|.
name|intValue
argument_list|()
decl_stmt|;
assert|assert
name|refCountInt
operator|>
literal|0
assert|;
name|refCountInt
operator|--
expr_stmt|;
if|if
condition|(
name|refCountInt
operator|==
literal|0
condition|)
block|{
name|refCounts
operator|.
name|remove
argument_list|(
name|gen
argument_list|)
expr_stmt|;
name|indexCommits
operator|.
name|remove
argument_list|(
name|gen
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|refCounts
operator|.
name|put
argument_list|(
name|gen
argument_list|,
name|refCountInt
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Increments the refCount for this {@link IndexCommit}. */
DECL|method|incRef
specifier|protected
specifier|synchronized
name|void
name|incRef
parameter_list|(
name|IndexCommit
name|ic
parameter_list|)
block|{
name|long
name|gen
init|=
name|ic
operator|.
name|getGeneration
argument_list|()
decl_stmt|;
name|Integer
name|refCount
init|=
name|refCounts
operator|.
name|get
argument_list|(
name|gen
argument_list|)
decl_stmt|;
name|int
name|refCountInt
decl_stmt|;
if|if
condition|(
name|refCount
operator|==
literal|null
condition|)
block|{
name|indexCommits
operator|.
name|put
argument_list|(
name|gen
argument_list|,
name|lastCommit
argument_list|)
expr_stmt|;
name|refCountInt
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|refCountInt
operator|=
name|refCount
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
name|refCounts
operator|.
name|put
argument_list|(
name|gen
argument_list|,
name|refCountInt
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Snapshots the last commit and returns it. Once a commit is 'snapshotted,' it is protected    * from deletion (as long as this {@link IndexDeletionPolicy} is used). The    * snapshot can be removed by calling {@link #release(IndexCommit)} followed    * by a call to {@link IndexWriter#deleteUnusedFiles()}.    *    *<p>    *<b>NOTE:</b> while the snapshot is held, the files it references will not    * be deleted, which will consume additional disk space in your index. If you    * take a snapshot at a particularly bad time (say just before you call    * forceMerge) then in the worst case this could consume an extra 1X of your    * total index size, until you release the snapshot.    *     * @throws IllegalStateException    *           if this index does not have any commits yet    * @return the {@link IndexCommit} that was snapshotted.    */
DECL|method|snapshot
specifier|public
specifier|synchronized
name|IndexCommit
name|snapshot
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|initCalled
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"this instance is not being used by IndexWriter; be sure to use the instance returned from writer.getConfig().getIndexDeletionPolicy()"
argument_list|)
throw|;
block|}
if|if
condition|(
name|lastCommit
operator|==
literal|null
condition|)
block|{
comment|// No commit yet, eg this is a new IndexWriter:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No index commit to snapshot"
argument_list|)
throw|;
block|}
name|incRef
argument_list|(
name|lastCommit
argument_list|)
expr_stmt|;
return|return
name|lastCommit
return|;
block|}
comment|/** Returns all IndexCommits held by at least one snapshot. */
DECL|method|getSnapshots
specifier|public
specifier|synchronized
name|List
argument_list|<
name|IndexCommit
argument_list|>
name|getSnapshots
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|indexCommits
operator|.
name|values
argument_list|()
argument_list|)
return|;
block|}
comment|/** Returns the total number of snapshots currently held. */
DECL|method|getSnapshotCount
specifier|public
specifier|synchronized
name|int
name|getSnapshotCount
parameter_list|()
block|{
name|int
name|total
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Integer
name|refCount
range|:
name|refCounts
operator|.
name|values
argument_list|()
control|)
block|{
name|total
operator|+=
name|refCount
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
return|return
name|total
return|;
block|}
comment|/** Retrieve an {@link IndexCommit} from its generation;    *  returns null if this IndexCommit is not currently    *  snapshotted  */
DECL|method|getIndexCommit
specifier|public
specifier|synchronized
name|IndexCommit
name|getIndexCommit
parameter_list|(
name|long
name|gen
parameter_list|)
block|{
return|return
name|indexCommits
operator|.
name|get
argument_list|(
name|gen
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
specifier|synchronized
name|IndexDeletionPolicy
name|clone
parameter_list|()
block|{
name|SnapshotDeletionPolicy
name|other
init|=
operator|(
name|SnapshotDeletionPolicy
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
name|other
operator|.
name|primary
operator|=
name|this
operator|.
name|primary
operator|.
name|clone
argument_list|()
expr_stmt|;
name|other
operator|.
name|lastCommit
operator|=
literal|null
expr_stmt|;
name|other
operator|.
name|refCounts
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|refCounts
argument_list|)
expr_stmt|;
name|other
operator|.
name|indexCommits
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|indexCommits
argument_list|)
expr_stmt|;
return|return
name|other
return|;
block|}
comment|/** Wraps each {@link IndexCommit} as a {@link    *  SnapshotCommitPoint}. */
DECL|method|wrapCommits
specifier|private
name|List
argument_list|<
name|IndexCommit
argument_list|>
name|wrapCommits
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|IndexCommit
argument_list|>
name|commits
parameter_list|)
block|{
name|List
argument_list|<
name|IndexCommit
argument_list|>
name|wrappedCommits
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|commits
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|IndexCommit
name|ic
range|:
name|commits
control|)
block|{
name|wrappedCommits
operator|.
name|add
argument_list|(
operator|new
name|SnapshotCommitPoint
argument_list|(
name|ic
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|wrappedCommits
return|;
block|}
comment|/** Wraps a provided {@link IndexCommit} and prevents it    *  from being deleted. */
DECL|class|SnapshotCommitPoint
specifier|private
class|class
name|SnapshotCommitPoint
extends|extends
name|IndexCommit
block|{
comment|/** The {@link IndexCommit} we are preventing from deletion. */
DECL|field|cp
specifier|protected
name|IndexCommit
name|cp
decl_stmt|;
comment|/** Creates a {@code SnapshotCommitPoint} wrapping the provided      *  {@link IndexCommit}. */
DECL|method|SnapshotCommitPoint
specifier|protected
name|SnapshotCommitPoint
parameter_list|(
name|IndexCommit
name|cp
parameter_list|)
block|{
name|this
operator|.
name|cp
operator|=
name|cp
expr_stmt|;
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
literal|"SnapshotDeletionPolicy.SnapshotCommitPoint("
operator|+
name|cp
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|delete
specifier|public
name|void
name|delete
parameter_list|()
block|{
synchronized|synchronized
init|(
name|SnapshotDeletionPolicy
operator|.
name|this
init|)
block|{
comment|// Suppress the delete request if this commit point is
comment|// currently snapshotted.
if|if
condition|(
operator|!
name|refCounts
operator|.
name|containsKey
argument_list|(
name|cp
operator|.
name|getGeneration
argument_list|()
argument_list|)
condition|)
block|{
name|cp
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getDirectory
specifier|public
name|Directory
name|getDirectory
parameter_list|()
block|{
return|return
name|cp
operator|.
name|getDirectory
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getFileNames
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getFileNames
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|cp
operator|.
name|getFileNames
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getGeneration
specifier|public
name|long
name|getGeneration
parameter_list|()
block|{
return|return
name|cp
operator|.
name|getGeneration
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getSegmentsFileName
specifier|public
name|String
name|getSegmentsFileName
parameter_list|()
block|{
return|return
name|cp
operator|.
name|getSegmentsFileName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getUserData
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getUserData
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|cp
operator|.
name|getUserData
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isDeleted
specifier|public
name|boolean
name|isDeleted
parameter_list|()
block|{
return|return
name|cp
operator|.
name|isDeleted
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getSegmentCount
specifier|public
name|int
name|getSegmentCount
parameter_list|()
block|{
return|return
name|cp
operator|.
name|getSegmentCount
argument_list|()
return|;
block|}
block|}
block|}
end_class
end_unit
