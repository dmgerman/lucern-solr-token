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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment
begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
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
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|document
operator|.
name|Document
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
name|IndexWriterConfig
operator|.
name|OpenMode
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
name|MockRAMDirectory
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
DECL|class|TestPersistentSnapshotDeletionPolicy
specifier|public
class|class
name|TestPersistentSnapshotDeletionPolicy
extends|extends
name|TestSnapshotDeletionPolicy
block|{
comment|// Keep it a class member so that getDeletionPolicy can use it
DECL|field|snapshotDir
specifier|private
name|Directory
name|snapshotDir
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|getDeletionPolicy
specifier|protected
name|SnapshotDeletionPolicy
name|getDeletionPolicy
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexWriter
operator|.
name|unlock
argument_list|(
name|snapshotDir
argument_list|)
expr_stmt|;
return|return
operator|new
name|PersistentSnapshotDeletionPolicy
argument_list|(
operator|new
name|KeepOnlyLastCommitDeletionPolicy
argument_list|()
argument_list|,
name|snapshotDir
argument_list|,
name|OpenMode
operator|.
name|CREATE
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDeletionPolicy
specifier|protected
name|SnapshotDeletionPolicy
name|getDeletionPolicy
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|snapshots
parameter_list|)
throws|throws
name|IOException
block|{
name|SnapshotDeletionPolicy
name|sdp
init|=
name|getDeletionPolicy
argument_list|()
decl_stmt|;
if|if
condition|(
name|snapshots
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|e
range|:
name|snapshots
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|sdp
operator|.
name|registerSnapshotInfo
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sdp
return|;
block|}
annotation|@
name|Override
annotation|@
name|Test
DECL|method|testExistingSnapshots
specifier|public
name|void
name|testExistingSnapshots
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|random
init|=
name|newRandom
argument_list|()
decl_stmt|;
name|int
name|numSnapshots
init|=
literal|3
decl_stmt|;
name|Directory
name|dir
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|PersistentSnapshotDeletionPolicy
name|psdp
init|=
operator|(
name|PersistentSnapshotDeletionPolicy
operator|)
name|getDeletionPolicy
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|getConfig
argument_list|(
name|random
argument_list|,
name|psdp
argument_list|)
argument_list|)
decl_stmt|;
name|prepareIndexAndSnapshots
argument_list|(
name|psdp
argument_list|,
name|writer
argument_list|,
name|numSnapshots
argument_list|,
literal|"snapshot"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|psdp
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Re-initialize and verify snapshots were persisted
name|psdp
operator|=
operator|new
name|PersistentSnapshotDeletionPolicy
argument_list|(
operator|new
name|KeepOnlyLastCommitDeletionPolicy
argument_list|()
argument_list|,
name|snapshotDir
argument_list|,
name|OpenMode
operator|.
name|APPEND
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|)
expr_stmt|;
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|getConfig
argument_list|(
name|random
argument_list|,
name|psdp
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertSnapshotExists
argument_list|(
name|dir
argument_list|,
name|psdp
argument_list|,
name|numSnapshots
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numSnapshots
argument_list|,
name|psdp
operator|.
name|getSnapshots
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|psdp
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|testIllegalSnapshotId
specifier|public
name|void
name|testIllegalSnapshotId
parameter_list|()
throws|throws
name|Exception
block|{
name|getDeletionPolicy
argument_list|()
operator|.
name|snapshot
argument_list|(
literal|"$SNAPSHOTS_DOC$"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInvalidSnapshotInfos
specifier|public
name|void
name|testInvalidSnapshotInfos
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Add the correct number of documents (1), but without snapshot information
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|snapshotDir
argument_list|,
name|getConfig
argument_list|(
name|newRandom
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
operator|new
name|PersistentSnapshotDeletionPolicy
argument_list|(
operator|new
name|KeepOnlyLastCommitDeletionPolicy
argument_list|()
argument_list|,
name|snapshotDir
argument_list|,
name|OpenMode
operator|.
name|APPEND
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should not have succeeded to read from an invalid Directory"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{     }
block|}
annotation|@
name|Test
DECL|method|testNoSnapshotInfos
specifier|public
name|void
name|testNoSnapshotInfos
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Initialize an empty index in snapshotDir - PSDP should initialize successfully.
operator|new
name|IndexWriter
argument_list|(
name|snapshotDir
argument_list|,
name|getConfig
argument_list|(
name|newRandom
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
operator|new
name|PersistentSnapshotDeletionPolicy
argument_list|(
operator|new
name|KeepOnlyLastCommitDeletionPolicy
argument_list|()
argument_list|,
name|snapshotDir
argument_list|,
name|OpenMode
operator|.
name|APPEND
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
DECL|method|testTooManySnapshotInfos
specifier|public
name|void
name|testTooManySnapshotInfos
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Write two documents to the snapshots directory - illegal.
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|snapshotDir
argument_list|,
name|getConfig
argument_list|(
name|newRandom
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
operator|new
name|PersistentSnapshotDeletionPolicy
argument_list|(
operator|new
name|KeepOnlyLastCommitDeletionPolicy
argument_list|()
argument_list|,
name|snapshotDir
argument_list|,
name|OpenMode
operator|.
name|APPEND
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"should not have succeeded to open an invalid directory"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSnapshotRelease
specifier|public
name|void
name|testSnapshotRelease
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|PersistentSnapshotDeletionPolicy
name|psdp
init|=
operator|(
name|PersistentSnapshotDeletionPolicy
operator|)
name|getDeletionPolicy
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|getConfig
argument_list|(
name|newRandom
argument_list|()
argument_list|,
name|psdp
argument_list|)
argument_list|)
decl_stmt|;
name|prepareIndexAndSnapshots
argument_list|(
name|psdp
argument_list|,
name|writer
argument_list|,
literal|1
argument_list|,
literal|"snapshot"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|psdp
operator|.
name|release
argument_list|(
literal|"snapshot0"
argument_list|)
expr_stmt|;
name|psdp
operator|.
name|close
argument_list|()
expr_stmt|;
name|psdp
operator|=
operator|new
name|PersistentSnapshotDeletionPolicy
argument_list|(
operator|new
name|KeepOnlyLastCommitDeletionPolicy
argument_list|()
argument_list|,
name|snapshotDir
argument_list|,
name|OpenMode
operator|.
name|APPEND
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have no snapshots !"
argument_list|,
literal|0
argument_list|,
name|psdp
operator|.
name|getSnapshots
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
